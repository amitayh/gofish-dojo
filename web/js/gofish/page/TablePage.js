define([
    'dojo/_base/declare',
    'dojo/_base/array',
    'dojo/_base/lang',
    'dojo/dom-construct',
    'dojo/Deferred',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dijit/_WidgetsInTemplateMixin',
    'gofish/data-service',
    'gofish/widget/Logger',
    'gofish/widget/Player',
    'dojo/text!gofish/template/TablePage.html'
], function(declare, array, lang, domConstruct, Deferred, _WidgetBase,
            _TemplatedMixin, _WidgetsInTemplateMixin, dataService,
            Logger, Player, template) {

    var UpdateInterval = 1500;
    
    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {
        
        templateString: template,

        updateTimer: null,

        players: {},

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
            var self = this,
                options = {lastEvent: this.lastEvent},
                callback = lang.hitch(this, 'getLastEvents');

            dataService.checkStatus(options)
                .then(function(response) {
                    self.lastEvent = response.totalEvents;
                    return self.playbackEvents(response.events);
                })
                .then(function() {
                    // Poll the server after all events have been played
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
            var self = this,
                player = new Player(event.player),
                deferred = new Deferred();

            this.players[player.getId()] = player;
            setTimeout(function() {
                domConstruct.place(player.domNode, self.playersList);
                self.logger.log('Player joined: ' + self.getPlayerName(player));
                deferred.resolve();
            }, 500);

            return deferred.promise;
        },

        playStartGameEvent: function() {
            var deferred = new Deferred(), logger = this.logger;

            setTimeout(function() {
                logger.log('Game started');
                deferred.resolve();
            }, 500);

            return deferred.promise;
        },

        getPlayerName: function(player) {
            var name = player.getName(true);
            return (player.getId() === this.playerId) ? '<strong>' + name + '</strong>' : name;
        }
        
    });
    
});