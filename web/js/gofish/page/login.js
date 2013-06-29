define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/on',
    'dojo/request',
    'dojo/dom-construct',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojox/html/entities',
    'dojo/text!gofish/template/login.html'
], function(declare, lang, on, request, domConstruct, _WidgetBase, _TemplatedMixin, entities, template) {
    
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
        
        onactivate: function() {
            this.getPlayersList();
        },
        
        ondeactivate: function() {
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
            request('checkStatus', {handleAs: 'json'}).then(function(response) {
                // Update list
                self.updatePlayersList(response.players);
                
                // Set update timer
                self.updateTimer = setTimeout(callback, UpdateInterval);
            });
        },
        
        updatePlayersList: function(players) {
            domConstruct.empty(this.playersList);
            for (var i = 0; i < players.totalNumPlayers; i++) {
                var li = domConstruct.create('li', null, this.playersList),
                    name = players.playerNames[i];
                
                if (i < players.numPlayers) {
                    li.innerHTML = entities.encode(name);
                }
            }
        }
        
    });
    
});