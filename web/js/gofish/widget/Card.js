define([
    'dojo/_base/declare',
    'dojo/dom-construct',
    'dojo/dom-class',
    'dijit/_WidgetBase'
], function(declare, domConstruct, domClass, _WidgetBase) {
    
    var ConcealedClassName = 'card-back';

    return declare(_WidgetBase, {

        buildRendering: function() {
            this.revealedClassName = 'card-' + this.cardName.replace(/ /g, '_').toLowerCase();
            this.domNode = domConstruct.create('li', {className: 'card'});
        },
        
        getId: function() {
            return this.cardId;
        },
        
        getName: function() {
            return this.cardName;
        },
        
        reveal: function() {
            this.domNode.innerHTML = this.cardName;
            domClass.add(this.domNode, this.revealedClassName);
            domClass.remove(this.domNode, ConcealedClassName);
        },
        
        conceal: function() {
            this.domNode.innerHTML = '';
            domClass.remove(this.domNode, this.revealedClassName);
            domClass.add(this.domNode, ConcealedClassName);
        }

    });

});