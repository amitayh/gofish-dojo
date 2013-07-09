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
            var player = new Player(event.player);
            this.players[player.get('id')] = player;
            domConstruct.place(player.domNode, this.playersList);
            this.logger.log('Player joined: ' + this.getPlayerName(player));
            if (player.get('id') === this.playerId) {
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

        getPlayerName: function(player) {
            var name = entities.encode(player.get('name'));
            return (player.get('id') === this.playerId) ? '<strong>' + name + '</strong>' : name;
        }
        
    });
    
});