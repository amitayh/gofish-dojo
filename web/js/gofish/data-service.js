define(['dojo/request'], function(request) {
    
    return {
        
        checkStatus: function() {
            return request('checkStatus', {handleAs: 'json'});
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