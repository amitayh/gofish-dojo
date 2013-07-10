define([
    'dojo/_base/declare',
    'dojo/dom-class',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dijit/_WidgetsInTemplateMixin',
    'dojox/html/entities',
    'gofish/widget/CardsList',
    'dojo/text!gofish/template/Player.html'
], function(declare, domClass, _WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin,
            entities, CardsList, template) {

    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {

        templateString: template,

        postCreate: function() {
            this.inherited(arguments);
            this.nameNode.innerHTML = entities.encode(this.playerName);
            this.numberOfCards.innerHTML = this.hand.length;
            this.handWidget.setCards(this.hand);
        },
        
        getId: function() {
            return this.playerId;
        },
        
        getName: function() {
            return this.playerName;
        },
        
        enableControls: function() {
            domClass.remove(this.controls, 'hide');
            this.handWidget.revealCards();
        },

        setCurrentPlayer: function(flag) {
            domClass[flag ? 'add' : 'remove'](this.domNode, 'current');
        }

    });

});