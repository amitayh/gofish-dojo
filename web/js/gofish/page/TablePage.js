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
                
                console.log(event); // Debug
                
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
                playerName: event.player.name,
                hand: event.player.hand
            });
            this.players[player.getId()] = player;
            domConstruct.place(player.domNode, this.playersList);
            this.logger.log('Player joined: ' + this.getPlayerName(player));
            if (player.getId() === this.playerId) {
                player.enableControls();
            }
        },

        playStartGameEvent: function() {
            this.logger.log('Game started');
        },
        
        playChangeTurnEvent: function(event) {
            if (this.currentPlayer) {
                this.currentPlayer.setCurrentPlayer(false);
            }
            var currentPlayer = this.currentPlayer = this.players[event.currentPlayerId];
            this.logger.log(this.getPlayerName(currentPlayer) + ' is playing');
            currentPlayer.setCurrentPlayer(true);
        },
        
        playAskCardEvent: function(event) {
            var player = this.players[event.playerId],
                askFrom = this.players[event.askFromPlayerId];
            
            this.logger.log(
                this.getPlayerName(player) + ' asked ' +
                this.getPlayerName(askFrom) + ' for card ' +
                this.getCardName(event.cardName)
            );
            
            return delayedPromise(2000);
        },
        
        playCardMovedEvent: function(event) {
            var from = this.players[event.fromPlayerId],
                to = this.players[event.toPlayerId];
            
            this.logger.log(
                this.getPlayerName(from) + ' gave ' +
                this.getCardName(event.cardName) + ' to ' +
                this.getPlayerName(to)
            );
    
            return delayedPromise(2000);
        },
        
        playGoFishEvent: function(event) {
            var player1 = this.players[event.player1Id],
                player2 = this.players[event.player2Id];
            
            this.logger.log(
                this.getPlayerName(player1) + ' tells ' +
                this.getPlayerName(player2) + ' to go fish '
            );
    
            return delayedPromise(2000);
        },

        getPlayerName: function(player) {
            var name = entities.encode(player.getName());
            return (player.getId() === this.playerId) ? '<strong>' + name + '</strong>' : name;
        },
        
        getCardName: function(cardName) {
            return '<em>' + entities.encode(cardName) + '</em>'
        }
        
    });
    
});