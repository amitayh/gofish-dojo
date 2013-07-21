define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/dom-class',
    'dojo/on',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojox/html/entities',
    'dojo/text!gofish/template/ConfigPage.html'
], function(declare, lang, domClass, on, _WidgetBase,
            _TemplatedMixin, entities, template) {
    
    var MinTotalPlayers = 3,
        MaxTotalPlayers = 6,
        MinHumanPlayers = 1;
    
    function limit(value, min, max) {
        return Math.min(Math.max(value, min), max);
    }

    return declare([_WidgetBase, _TemplatedMixin], {
        
        templateString: template,
        
        model: {
            // Default values
            humanPlayers: 1,
            computerPlayers: 5,
            allowMultipleRequests: true,
            forceShowOfSeries: false
        },
        
        postCreate: function() {
            this.inherited(arguments);
            this.updateForm();
            
            // Listen to messages from the upload iframe
            on(window, 'message', lang.hitch(this, 'uploadXmlResult'));
        },
        
        uploadXmlResult: function(event) {
            if (event.data.type === 'UploadXmlResult') {
                this.file.value = null;
                this.clearError();
                if (!event.data.success) {
                    this.setError(event.data.message);
                } else {
                    this.emit('StartGame', {});
                }
            }
        },
        
        setError: function(message) {
            this.uploadXmlError.innerHTML = entities.encode(message);
            domClass.remove(this.uploadXmlError, 'hide');
        },
        
        clearError: function() {
            this.uploadXmlError.innerHTML = '';
            domClass.add(this.uploadXmlError, 'hide');
        },
        
        updateForm: function() {
            var model = this.model;
            this.humanPlayers.value = model.humanPlayers;
            this.computerPlayers.value = model.computerPlayers;
            this.allowMultipleRequests.checked = model.allowMultipleRequests;
            this.forceShowOfSeries.checked = model.forceShowOfSeries;
        },
        
        setHumanPlayers: function() {
            var humanPlayers = parseInt(this.humanPlayers.value);
            if (!isNaN(humanPlayers)) {
                var model = this.model,
                    computerPlayers = model.computerPlayers,
                    min = Math.max(MinHumanPlayers, MinTotalPlayers - computerPlayers);

                humanPlayers = limit(
                    humanPlayers,                   // Value
                    min,                            // Min
                    MaxTotalPlayers                 // Max
                );
                computerPlayers = limit(
                    computerPlayers,                // Value
                    0,                              // Min
                    MaxTotalPlayers - humanPlayers  // Max
                );

                model.humanPlayers = humanPlayers;
                model.computerPlayers = computerPlayers;
            }
            this.updateForm();
        },
        
        setComputerPlayers: function() {
            var computerPlayers = parseInt(this.computerPlayers.value);
            if (!isNaN(computerPlayers)) {
                var model = this.model,
                    humanPlayers = model.humanPlayers;

                computerPlayers = limit(
                    computerPlayers,                    // Value
                    MinTotalPlayers - humanPlayers,     // Min
                    MaxTotalPlayers - MinHumanPlayers   // Max
                );
                humanPlayers = limit(
                    humanPlayers,                       // Value
                    MinHumanPlayers,                    // Min
                    MaxTotalPlayers - computerPlayers   // Max
                );

                model.humanPlayers = humanPlayers;
                model.computerPlayers = computerPlayers;
            }
            this.updateForm();
        },
        
        setAllowMultipleRequests: function() {
            this.model.allowMultipleRequests = this.allowMultipleRequests.checked;
        },
        
        setForceShowOfSeries: function() {
            this.model.forceShowOfSeries = this.forceShowOfSeries.checked;
        },
                
        startGame: function() {
            this.emit('StartGame', {data: this.model});
        },
        
        onStartGame: function(e) {}
        
    });
    
});