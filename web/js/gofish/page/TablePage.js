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
    
    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {
        
        templateString: template,

        updateTimer: null,

        onLoad: function() {
            this.players = {};
            this.player = this.currentPlayer = null;
            this.lastEvent = 0;
            domConstruct.empty(this.playersList);
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
                playerName = this.getPlayerName(player),
                askFrom = this.players[event.askFrom.id],
                askFromName = this.getPlayerName(askFrom),
                cardName = this.getCardName(event.cardName);
            
            this.logger.log(playerName + ' asked ' + askFromName + ' for card ' + cardName);
            
            return player.say(askFromName + ', do you have ' + cardName + '?');
        },
        
        playCardMovedEvent: function(event) {
            var from = this.players[event.from.id],
                fromName = this.getPlayerName(from),
                to = this.players[event.to.id],
                toName = this.getPlayerName(to),
                cardName = this.getCardName(event.card.name),
                self = this;
            
            this.logger.log(fromName + ' gave ' + cardName + ' to ' + toName);
    
            return from.say('Yes I do, here you go').then(function() {
                return self.animateCardMove(from, to, event.card.id);
            });
        },
        
        animateCardMove: function(from, to, cardId) {
            var initialPosition = from.hand.getCardPosition(cardId),
                card = from.removeCard(cardId);
            
            card[to === this.player ? 'reveal' : 'conceal']();
            
            return to.animateAddCard(card, initialPosition);
        },
        
        playSeriesDroppedEvent: function(event) {
            var player = this.players[event.player.id],
                series = event.series;
            
            player.dropSeries(series);
            
            this.logger.log(
                this.getPlayerName(player) + ' dropped a series of ' +
                this.getSeriesName(series)
            );
        },
        
        playGoFishEvent: function(event) {
            var player1 = this.players[event.player1.id],
                player1Name = this.getPlayerName(player1),
                player2 = this.players[event.player2.id],
                player2Name = this.getPlayerName(player2);
            
            this.logger.log(player1Name + ' tells ' + player2Name + ' to go fish');
    
            return player1.say('Go fish!');
        },
        
        playSkipTurnEvent: function(event) {
            var player = this.players[event.player.id];
            this.logger.log(this.getPlayerName(player) + ' skips his turn');
        },
        
        playQuitGameEvent: function(event) {
            var player = this.players[event.player.id];
            player.quitGame();
            this.logger.log(this.getPlayerName(player) + ' is out of the game!');
        },
        
        playGameOverEvent: function(event) {
            var winner = this.players[event.winner.id];
            
            this.logger.log(
                'Game over! Winner is ' + this.getPlayerName(winner) +
                ' with ' + event.winnerCompleteSeries + ' complete series'
            );
            
            var self = this;
            setTimeout(function() {
                self.emit('GameOver', {});
            }, UpdateInterval);
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
        },
        
        onGameOver: function(e) {}
        
    });
    
});