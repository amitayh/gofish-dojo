define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/dom-construct',
    'gofish/data-service',
    'gofish/page/config',
    'gofish/page/login',
    'gofish/page/table'
], function(declare, lang, domConstruct, dataService, ConfigPage, LoginPage, TablePage) {

    return declare(null, {
        
        currentPage: null,
        
        constructor: function(container) {
            this.container = container;
            this.pages = {
                config: new ConfigPage(),
                login: new LoginPage(),
                table: new TablePage()
            };
            this.init();
        },
        
        init: function() {
            this.pages.config.on('start', lang.hitch(this, 'startGame'));
            this.pages.login.on('joinGame', lang.hitch(this, 'joinGame'));
        },
        
        run: function() {
            var self = this;
            dataService.checkStatus().then(function(response) {
                if (response.status === 'IDLE') {
                    // Configure a new game
                    self.loadPage('config');
                } else if (response.status === 'CONFIGURED') {
                    // Game is configured, waiting for human players
                    var loginPage = self.pages.login;
                    if (response.playerId) {
                        // Already logged in
                        loginPage.setAlert('Successfully joined game! Waiting for other players...', 'success');
                        loginPage.clearForm();
                    } else {
                        // Join a game that was configured by another player
                        loginPage.setAlert('Game already created by another player, please enter your name to join');
                    }
                    self.loadPage('login');
                }
            });
        },
        
        loadPage: function(page) {
            var newPage = this.pages[page];
            if (this.currentPage) {
                this.currentPage.emit('deactivate', {});
                domConstruct.place(newPage.domNode, this.currentPage.domNode, 'replace');
            } else {
                domConstruct.place(newPage.domNode, this.container);
            }
            this.currentPage = newPage;
            this.currentPage.emit('activate', {});
        },
        
        startGame: function() {
            var self = this, configData = this.pages.config.getData();
            dataService.configure(configData).then(function(response) {
                if (response.status === 'CONFIGURED') {
                    if (response.success) {
                        var loginPage = self.pages.login,
                            message = 'Game was successfully created, please enter your name to join',
                            type = 'success';

                        loginPage.setAlert(message, type);
                    }
                    self.loadPage('login');
                }
            });
        },
        
        joinGame: function(event) {
            var options = {
                data: {name: event.name},
                method: 'post',
                handleAs: 'json'
            };
            
            var loginPage = this.pages.login;
            request('login', options).then(function(response) {
                if (response.success) {
                    var message = 'Successfully joined game!',
                        players = response.players;
                    
                    if (players.numPlayers < players.totalNumPlayers) {
                        message += ' Waiting for other players...';
                    }
                    
                    loginPage.updatePlayersList(players);
                    loginPage.setAlert(message, 'success');
                    loginPage.clearForm();
                    
                } else {
                    loginPage.setAlert(response.message, 'error');
                }
                // Alreay logged in
                // 1) login OK
                // 2) name taken
                // 3) game is full
            });
        }

    });

});