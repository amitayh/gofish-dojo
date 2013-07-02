define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/Deferred',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'gofish/data-service',
    'dojo/text!gofish/template/table.html'
], function(declare, lang, Deferred, _WidgetBase, _TemplatedMixin,
            dataService, template) {

    var UpdateInterval = 1500;
    
    return declare([_WidgetBase, _TemplatedMixin], {
        
        templateString: template,

        updateTimer: null,

        onLoad: function() {
            this.lastEventId = 0;
            this.getLastEvents();
        },

        onUnload: function() {
            clearTimeout(this.updateTimer);
        },

        getLastEvents: function() {
            var self = this, callback = lang.hitch(this, 'getLastEvents');
            dataService.checkStatus(this.lastEventId)
                .then(function(response) {
                    self.lastEventId = response.lastEventId;
                    return self.playbackEvents(response.events);
                })
                .then(function() {
                    self.updateTimer = setTimeout(callback, UpdateInterval);
                });
        },

        playbackEvents: function(events) {
            return true;
        }
        
    });
    
});