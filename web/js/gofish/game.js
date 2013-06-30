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
            // Attach event listeners
            this.pages.config.on('startGame', lang.hitch(this, 'startGame'));
            this.pages.login.on('joinGame', lang.hitch(this, 'joinGame'));
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
                        self[loggedIn ? 'loadTablePage' : 'gameFullError']();
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
                    loginPage.updatePlayersList(response.players, response.totalPlayers);
                    self.joinGameSuccess();
                } else {
                    loginPage.setAlert(response.message, 'error');
                }
                // Alreay logged in
                // 1) login OK
                // 2) name taken
                // 3) game is full
            });
        },

        loadPage: function(page) {
            var newPage = this.pages[page];
            if (this.currentPage) {
                this.currentPage.emit('Unload', {});
                domConstruct.place(newPage.domNode, this.currentPage.domNode, 'replace');
            } else {
                domConstruct.place(newPage.domNode, this.container);
            }
            this.currentPage = newPage;
            this.currentPage.emit('Load', {});
        },

        loadTablePage: function() {
            this.loadPage('table');
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