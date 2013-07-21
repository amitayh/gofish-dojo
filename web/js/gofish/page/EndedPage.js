define([
    'dojo/_base/declare',
    'dojo/topic',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'gofish/data-service',
    'dojo/text!gofish/template/EndedPage.html'
], function(declare, topic, _WidgetBase, _TemplatedMixin, dataService, template) {
    
    return declare([_WidgetBase, _TemplatedMixin], {
        
        templateString: template,
        
        resetGame: function() {
            dataService.resetGame().then(function() {
                topic.publish('gofish/restart', {});
            });
        }
        
    });
    
});