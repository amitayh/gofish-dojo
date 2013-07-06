define([
    'dojo/_base/declare',
    'dojo/dom-construct',
    'dijit/_WidgetBase'
], function(declare, domConstruct, _WidgetBase) {

    return declare(_WidgetBase, {

        buildRendering: function() {
            this.domNode = domConstruct.create('ul', {className: 'cards'});
        }

    });

});