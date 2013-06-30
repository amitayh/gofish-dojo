define([
    'dojo/_base/declare',
    'dojo/on',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojo/text!gofish/template/config.html'
], function(declare, on, _WidgetBase, _TemplatedMixin, template) {
    
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
                
        getData: function() {
            return this.model;
        },
        
        onStartGame: function() {
            // Fires when the "start game" button is clicked
        }
        
    });
    
});