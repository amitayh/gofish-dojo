define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/dom-construct',
    'dojo/topic',
    'gofish/data-service',
    'gofish/page/ConfigPage',
    'gofish/page/LoginPage',
    'gofish/page/TablePage',
    'gofish/page/EndedPage',
    'gofish/page/ErrorPage'
], function(declare, lang, domConstruct, topic, dataService,
            ConfigPage, LoginPage, TablePage, EndedPage, ErrorPage) {

    return declare(null, {
        
        currentPage: null,
        
        constructor: function(container) {
            this.container = container;
            this.pages = {
                config: new ConfigPage(),
                login: new LoginPage(),
                table: new TablePage(),
                ended: new EndedPage(),
                error: new ErrorPage()
            };
            this.init();
        },
        
        init: function() {
            var configPage = this.pages.config,
                loginPage = this.pages.login,
                tablePage = this.pages.table;

            // Attach event listeners
            configPage.on('startGame', lang.hitch(this, 'startGame'));
            loginPage.on('joinGame', lang.hitch(this, 'joinGame'));
            loginPage.on('gameStarted', lang.hitch(this, 'gameStarted'));
            tablePage.on('gameOver', lang.hitch(this, 'gameOver'));
            topic.subscribe('gofish/restart', lang.hitch(this, 'restartGame'));
        },

        run: function() {
            var self = this;
            dataService.checkStatus().then(function(response) {
                var playerId = response.playerId;
                switch (response.status) {
                    case 'IDLE':
                        // Configure a new game
                        self.loadPage('config');
                        break;

                    case 'CONFIGURED':
                        // Game is configured, waiting for human players
                        var xml = (response.humanPlayersNames !== undefined);
                        self.pages.login.xmlConfig(xml);
                        self[playerId ? 'joinGameSuccess' : 'gameAlreadyConfigured']();
                        self.loadPage('login');
                        break;

                    case 'STARTED':
                        // Game started
                        self.gameStarted({playerId: playerId});
                        break;
                    
                    case 'ENDED':
                        // Game ended
                        self.gameOver();
                }
            });
        },
        
        restartGame: function() {
            this.loadPage('config');
        },
        
        startGame: function(event) {
            var xml = event.xml;
            this.pages.login.xmlConfig(xml);
            if (xml) {
                // Game configured with XML file
                this.startGameSuccess();
                this.loadPage('login');
            } else {
                // Manual configuration
                var self = this;
                dataService.configure(event.data).then(function(response) {
                    switch (response.status) {
                        case 'CONFIGURED':
                            var success = response.success;
                            self[success ? 'startGameSuccess' : 'gameAlreadyConfigured']();
                            self.loadPage('login');
                            break;

                        case 'STARTED':
                            self.gameFullError();
                            break;
                        
                        case 'ENDED':
                            self.gameOver();
                            break;
                            
                        default:
                            self.loadPage('config');
                            break;
                    }
                });
            }
        },
        
        joinGame: function(event) {            
            var self = this;
            dataService.login(event.name).then(function(response) {
                var loginPage = self.pages.login;
                if (response.success) {
                    loginPage.addPlayerFromForm();
                    self.joinGameSuccess();
                } else {
                    loginPage.setAlert(response.message, 'error');
                }
            });
        },

        gameStarted: function(event) {
            if (event.playerId) {
                // Player is logged in - show game table
                this.pages.table.setPlayerId(event.playerId);
                this.loadPage('table');
            } else {
                // Game is full - show error page
                this.gameFullError();
            }
        },

        loadPage: function(page) {
            var newPage = this.pages[page];
            if (this.currentPage) {
                // Unload previous page
                this.currentPage.emit('Unload', {});
                domConstruct.place(newPage.domNode, this.currentPage.domNode, 'replace');
            } else {
                domConstruct.place(newPage.domNode, this.container);
            }
            // Load new page
            newPage.emit('Load', {});
            this.currentPage = newPage;
        },
        
        gameOver: function() {
            this.loadPage('ended');
        },

        gameFullError: function() {
            this.pages.error.setError('Game already started, please try again later');
            this.loadPage('error');
        },

        startGameSuccess: function() {
            this.pages.login.setAlert('Game was successfully created, please enter your name to join', 'success');
        },

        joinGameSuccess: function() {
            var loginPage = this.pages.login;
            loginPage.setAlert('Successfully joined game! Waiting for other players...', 'success');
            loginPage.clearForm();
        },

        gameAlreadyConfigured: function() {
            this.pages.login.setAlert('Game already created by another player, please enter your name to join');
        }

    });

});