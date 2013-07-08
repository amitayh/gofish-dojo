define([
    'dojo/_base/declare',
    'dojo/dom-construct',
    'dijit/_WidgetBase',
    'gofish/widget/Card'
], function(declare, domConstruct, _WidgetBase, Card) {

    return declare(_WidgetBase, {

        buildRendering: function() {
            this.domNode = domConstruct.create('ul', {
                className: 'cards-list clearfix'
            });
        },
        
        setCards: function(cards) {
            var container = this.domNode, card;
            this.cards = {};
            for (var i = 0, l = cards.length; i < l; i++) {
                card = new Card(cards[i]);
                this.cards[card.get('id')] = card;
                domConstruct.place(card.domNode, container);
            }
        },
        
        revealCards: function() {
            var cards = this.cards;
            for (var id in cards) {
                if (cards.hasOwnProperty(id)) {
                    cards[id].reveal();
                }
            }
        }

    });

});