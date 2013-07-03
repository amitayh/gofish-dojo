define([
    'dojo/_base/declare',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojox/html/entities',
    'dojo/text!gofish/template/ErrorPage.html'
], function(declare, _WidgetBase, _TemplatedMixin, entities, template) {
    
    return declare([_WidgetBase, _TemplatedMixin], {
        
        templateString: template,

        setError: function(error) {
            this.errorMessage.innerHTML = entities.encode(error);
        }
        
    });
    
});