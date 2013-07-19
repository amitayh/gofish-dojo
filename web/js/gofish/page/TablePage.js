define([
    'dojo/_base/declare',
    'dojo/_base/array',
    'dojo/_base/lang',
    'dojo/dom-construct',
    'dojo/topic',
    'dojo/Deferred',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dijit/_WidgetsInTemplateMixin',
    'dojox/html/entities',
    'gofish/data-service',
    'gofish/widget/Logger',
    'gofish/widget/Player',
    'dojo/text!gofish/template/TablePage.html'
], function(declare, array, lang, domConstruct, topic, Deferred,
            _WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin,
            entities, dataService, Logger, Player, template) {

    var UpdateInterval = 1500;
    
    function delayedPromise(delay) {
        var deferred = new Deferred();
        setTimeout(deferred.resolve, delay);
        return deferred.promise;
    }
    
    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {
        
        templateString: template,

        updateTimer: null,

        players: {},
        
        player: null,
        
        currentPlayer: null,

        onLoad: function() {
            this.lastEvent = 0;
            this.getLastEvents();
        },

        onUnload: function() {
            clearTimeout(this.updateTimer);
        },

        setPlayerId: function(playerId) {
            this.playerId = playerId;
        },

        getLastEvents: function() {
            var self = this, options = {lastEvent: this.lastEvent};
            dataService.checkStatus(options)
                .then(function(response) {
                    self.lastEvent = response.totalEvents;
                    return self.playbackEvents(response.events);
                })
                .then(function() {
                    // Poll the server after all events have been played
                    var callback = lang.hitch(self, 'getLastEvents');
                    self.updateTimer = setTimeout(callback, UpdateInterval);
                });
        },

        playbackEvents: function(events) {
            var self = this,
                deferred = new Deferred(),
                promise = deferred.promise;

            // Playback events in sequence using deferred/promise API
            array.forEach(events, function(event) {
                
                console.debug('Event', event); // Debug
                
                var method = self['play' + event.type];
                if (method) {
                    // Add method to promise chain
                    promise = promise.then(function() {
                        return method.call(self, event);
                    });
                }
            });

            // Resolve first deferred to start the chain
            deferred.resolve();

            // Return last promise
            return promise;
        },

        playPlayerJoinEvent: function(event) {
            var player = new Player({
                playerId: event.player.id,
                playerName: event.player.name
            });
            this.players[player.getId()] = player;
            domConstruct.place(player.domNode, this.playersList);
            this.logger.log('Player joined: ' + this.getPlayerName(player));
            if (player.getId() === this.playerId) {
                this.player = player;
            }
        },

        playStartGameEvent: function(event) {
            var playersCards = event.playersCards;
            for (var playerId in playersCards) {
                if (playersCards.hasOwnProperty(playerId)) {
                    var player = this.players[playerId];
                    player.setHand(playersCards[playerId]);
                }
            }
            
            this.logger.log('Game started');
            this.player.initControls(this.players);
        },
        
        playChangeTurnEvent: function(event) {
            if (this.currentPlayer) {
                this.currentPlayer.setCurrentPlayer(false);
            }
            var currentPlayer = this.currentPlayer = this.players[event.currentPlayer.id];
            this.logger.log(this.getPlayerName(currentPlayer) + ' is playing');
            currentPlayer.setCurrentPlayer(true);
            
            this.player.hand.disableSelection();
            if (currentPlayer === this.player) {
                this.player.hand.enableSelection();
            }
        },
        
        playAskCardEvent: function(event) {
            var player = this.players[event.player.id],
                askFrom = this.players[event.askFrom.id];
            
            this.logger.log(
                this.getPlayerName(player) + ' asked ' +
                this.getPlayerName(askFrom) + ' for card ' +
                this.getCardName(event.cardName)
            );
            
            return delayedPromise(500);
        },
        
        playCardMovedEvent: function(event) {
            var from = this.players[event.from.id],
                to = this.players[event.to.id];
            
            var card = from.removeCard(event.card.id);
            card[to === this.player ? 'reveal' : 'conceal']();
            to.addCard(card);
            
            this.logger.log(
                this.getPlayerName(from) + ' gave ' +
                this.getCardName(event.card.name) + ' to ' +
                this.getPlayerName(to)
            );
    
            return delayedPromise(500);
        },
        
        playSeriesDroppedEvent: function(event) {
            var player = this.players[event.player.id],
                series = event.series;
            
            player.dropSeries(series);
            
            this.logger.log(
                this.getPlayerName(player) + ' dropped a series of ' +
                this.getSeriesName(series)
            );
    
            return delayedPromise(500);
        },
        
        playGoFishEvent: function(event) {
            var player1 = this.players[event.player1.id],
                player2 = this.players[event.player2.id];
            
            this.logger.log(
                this.getPlayerName(player1) + ' tells ' +
                this.getPlayerName(player2) + ' to go fish '
            );
    
            return delayedPromise(500);
        },

        getPlayerName: function(player) {
            var name = entities.encode(player.getName());
            return (player === this.player) ? '<strong>' + name + '</strong>' : name;
        },
        
        getCardName: function(cardName) {
            return '<em>' + entities.encode(cardName) + '</em>';
        },
        
        getSeriesName: function(series) {
            return '<em>' + entities.encode(series.property) + "</em>'s";
        }
        
    });
    
});