define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/dom-construct',
    'gofish/data-service',
    'gofish/page/config',
    'gofish/page/login',
    'gofish/page/table',
    'gofish/page/error'
], function(declare, lang, domConstruct, dataService,
            ConfigPage, LoginPage, TablePage, ErrorPage) {

    return declare(null, {
        
        currentPage: null,
        
        constructor: function(container) {
            this.container = container;
            this.pages = {
                config: new ConfigPage(),
                login: new LoginPage(),
                table: new TablePage(),
                error: new ErrorPage()
            };
            this.init();
        },
        
        init: function() {
            var configPage = this.pages.config,
                loginPage = this.pages.login;

            // Attach event listeners
            configPage.on('startGame', lang.hitch(this, 'startGame'));
            loginPage.on('joinGame', lang.hitch(this, 'joinGame'));
            loginPage.on('gameStarted', lang.hitch(this, 'gameStarted'));
        },

        run: function() {
            var self = this;
            dataService.checkStatus().then(function(response) {
                var loggedIn = !!response.playerId;
                switch (response.status) {
                    case 'IDLE':
                        // Configure a new game
                        self.loadPage('config');
                        break;

                    case 'CONFIGURED':
                        // Game is configured, waiting for human players
                        self[loggedIn ? 'joinGameSuccess' : 'gameAlreadyConfigured']();
                        self.loadPage('login');
                        break;

                    case 'STARTED':
                        // Game started
                        self.gameStarted({loggedIn: loggedIn});
                        break;
                }
            });
        },
        
        startGame: function() {
            var self = this, configData = this.pages.config.getData();
            dataService.configure(configData).then(function(response) {
                switch (response.status) {
                    case 'CONFIGURED':
                        var success = response.success;
                        self[success ? 'startGameSuccess' : 'gameAlreadyConfigured']();
                        self.loadPage('login');
                        break;

                    case 'STARTED':
                        self.gameFullError();
                        break;
                }
            });
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
            if (event.loggedIn) {
                // Player is logged in - show game table
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

        gameFullError: function() {
            this.pages.error.setError('Game already started');
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