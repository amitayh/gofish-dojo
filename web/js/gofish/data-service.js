define(['dojo/request'], function(request) {
    
    return {
        
        checkStatus: function(lastEventId) {
            var options = {handleAs: 'json'};
            if (lastEventId !== undefined) {
                options.data = {lastEventId: lastEventId};
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
        }
        
    };
    
});