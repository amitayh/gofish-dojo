define([
    'dojo/_base/declare',
    'dojo/dom-construct',
    'dojo/dom-class',
    'dijit/_WidgetBase',
], function(declare, domConstruct, domClass, _WidgetBase) {

    return declare(_WidgetBase, {

        buildRendering: function() {
            this.domNode = domConstruct.create('li', {
                className: 'card',
                innerHTML: 'Card'
            });
        },
        
        reveal: function() {
            var className = this.name.replace(/ /g, '_').toLowerCase();
            this.domNode.innerHTML = this.name;
            domClass.add(this.domNode, className);
        }

    });

});