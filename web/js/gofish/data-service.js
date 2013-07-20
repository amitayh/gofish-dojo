define(['dojo/_base/lang', 'dojo/request'], function(lang, request) {
    
    return {
        
        checkStatus: function(data) {
            var options = {handleAs: 'json'};
            if (data !== undefined) {
                options.data = data;
                options.method = 'post';
            }
            return request('checkStatus', options);
        },
        
        configure: function(configData) {
            return request('configure', {
                data: configData,
                method: 'post',
                handleAs: 'json'
            });
        },
        
        login: function(name) {
            return request('login', {
                data: {name: name},
                method: 'post',
                handleAs: 'json'
            });
        },
        
        getAvailableCards: function() {
            return request('getAvailableCards', {handleAs: 'json'});
        },
        
        performPlayerAction: function(action, args) {
            var data = {action: action};
            if (args !== undefined) {
                lang.mixin(data, args);
            }
            return request('performPlayerAction', {
                data: data,
                method: 'post',
                handleAs: 'json'
            });
        },
        
        askCard: function(askFrom, cardName) {
            return this.performPlayerAction('askCard', {
                askFrom: askFrom,
                cardName: cardName
            });
        },
        
        dropSeries: function(cards) {
            var cardNames = [];
            for (var id in cards) {
                if (cards.hasOwnProperty(id)) {
                    cardNames.push(cards[id].getName());
                }
            }
            return this.performPlayerAction('dropSeries', {
                cards: cardNames.join(',')
            });
        },
        
        skipTurn: function() {
            return this.performPlayerAction('skipTurn');
        },
        
        quitGame: function() {
            return this.performPlayerAction('quitGame');
        }
        
    };
    
});