define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/_base/event',
    'dojo/dom-construct',
    'dojo/dom-class',
    'dojo/query',
    'dojo/topic',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojox/html/entities',
    'gofish/data-service',
    'dojo/text!gofish/template/LoginPage.html'
], function(declare, lang, event, domConstruct, domClass, query, topic,
            _WidgetBase, _TemplatedMixin, entities, dataService, template) {
    
    var UpdateInterval = 1500;

    return declare([_WidgetBase, _TemplatedMixin], {
        
        templateString: template,
        
        updateTimer: null,
        
        xml: false,
        
        xmlConfig: function(flag) {
            this.xml = flag;
            domClass[flag ? 'add' : 'remove'](this.nameInput, 'hide');
            domClass[flag ? 'remove' : 'add'](this.nameSelect, 'hide');
        },
        
        submitForm: function(e) {
            var name = this.xml ? this.nameSelect.value : this.nameInput.value;
            if (name === '') {
                this.setAlert('Player name is required', 'error');
            } else {
                this.emit('JoinGame', {name: name});
            }

            // Stop regular submission
            event.stop(e);
        },
        
        clearForm: function() {
            this.nameInput.blur();
            this.nameInput.disabled = true;
            this.nameInput.placeholder = '';
            this.nameInput.value = '';
            this.nameSelect.blur();
            this.nameSelect.disabled = true;
            domConstruct.empty(this.nameSelect);
            this.joinButton.disabled = true;
        },
        
        onLoad: function() {
            this.getPlayersList();
            this.nameInput.focus();
        },
        
        onUnload: function() {
            clearTimeout(this.updateTimer);
        },
        
        setAlert: function(message, type) {
            var className = 'alert';
            if (type !== undefined) {
                className += ' alert-' + type;
            }
            this.alert.innerHTML = entities.encode(message);
            this.alert.className = className;
        },
        
        getPlayersList: function() {
            var self = this,
                options = {includePlayers: true},
                callback = lang.hitch(this, 'getPlayersList');

            dataService.checkStatus(options).then(function(response) {
                self.updateHumanPlayersNames(response.humanPlayersNames);
                switch (response.status) {
                    case 'CONFIGURED':
                        // Update list and reset update timer
                        self.updatePlayersList(response.players, response.totalPlayers);
                        self.updateTimer = setTimeout(callback, UpdateInterval);
                        break;

                    case 'STARTED':
                        self.emit('GameStarted', {playerId: response.playerId});
                        break;
                    
                    default:
                        topic.publish('gofish/restart', {});
                        break;
                }
            });
        },
        
        updateHumanPlayersNames: function(names) {
            if (names !== undefined) {
                var select = this.nameSelect;
                domConstruct.empty(select);
                for (var i = 0, l = names.length; i < l; i++) {
                    var options = {innerHTML: entities.encode(names[i])};
                    domConstruct.create('option', options, select);
                }
            }
        },
        
        updatePlayersList: function(players, totalPlayers) {
            var defaultAttrs = {className: 'waiting'};

            domConstruct.empty(this.playersList);
            for (var i = 0; i < totalPlayers; i++) {
                var li = domConstruct.create('li', defaultAttrs, this.playersList),
                    player = players[i];
                
                if (player !== undefined) {
                    this.setPlayerName(li, player.name);
                }
            }
        },

        addPlayerFromForm: function() {
            // Find a "free" list item (waiting for players to join)
            var waiting = query('.waiting', this.playersList);
            if (waiting.length) {
                // Set player name from input
                this.setPlayerName(waiting[0], this.nameInput.value);
            }
        },

        setPlayerName: function(li, name) {
            li.innerHTML = entities.encode(name);
            domClass.remove(li, 'waiting');
        },

        onJoinGame: function(e) {},

        onGameStarted: function(e) {}
        
    });
    
});