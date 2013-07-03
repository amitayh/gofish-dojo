define(['dojo/request'], function(request) {
    
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
        }
        
    };
    
});