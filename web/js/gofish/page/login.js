define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/dom-construct',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojox/html/entities',
    'gofish/data-service',
    'dojo/text!gofish/template/login.html'
], function(declare, lang, domConstruct, _WidgetBase, _TemplatedMixin,
            entities, dataService, template) {
    
    var UpdateInterval = 2500;

    return declare([_WidgetBase, _TemplatedMixin], {
        
        baseClass: 'login-view',
        
        templateString: template,
        
        updateTimer: null,
        
        postCreate: function() {
            this.inherited(arguments);
        },
        
        joinGame: function() {
            var name = this.nameInput.value;
            if (name === '') {
                this.setAlert('Player name is required', 'error');
            } else {
                this.emit('JoinGame', {name: this.nameInput.value});
            }
        },
        
        onJoinGame: function(event) {},
        
        clearForm: function() {
            this.nameInput.disabled = true;
            this.nameInput.placeholder = '';
            this.nameInput.value = '';
            this.joinButton.disabled = true;
        },
        
        onLoad: function() {
            this.getPlayersList();
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
            var self = this, callback = lang.hitch(this, 'getPlayersList');
            dataService.checkStatus().then(function(response) {
                switch (response.status) {
                    case 'CONFIGURED':
                        // Update list
                        self.updatePlayersList(response.players, response.totalPlayers);

                        // Set update timer
                        self.updateTimer = setTimeout(callback, UpdateInterval);

                        break;

                    case 'STARTED':
                        break;
                }
            });
        },
        
        updatePlayersList: function(players, totalPlayers) {
            domConstruct.empty(this.playersList);
            for (var i = 0; i < totalPlayers; i++) {
                var li = domConstruct.create('li', null, this.playersList),
                    player = players[i];
                
                if (player !== undefined) {
                    li.innerHTML = entities.encode(player.name);
                }
            }
        }
        
    });
    
});