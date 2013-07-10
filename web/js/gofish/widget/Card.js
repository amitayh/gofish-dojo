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
        
        getId: function() {
            return this.cardId;
        },
        
        getName: function() {
            return this.cardName;
        },
        
        reveal: function() {
            var className = this.cardName.replace(/ /g, '_').toLowerCase();
            this.domNode.innerHTML = this.cardName;
            domClass.add(this.domNode, className);
        }

    });

});