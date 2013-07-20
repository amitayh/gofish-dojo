define([
    'dojo/_base/declare',
    'dojo/on',
    'dojo/dom-construct',
    'dojo/dom-class',
    'dijit/registry',
    'dijit/_WidgetBase'
], function(declare, on, domConstruct, domClass, registry, _WidgetBase) {

    return declare(_WidgetBase, {
        
        length: 0,
        
        selected: 0,
        
        maxSelected: 0,
        
        selectionEnabled: false,

        buildRendering: function() {
            this.cards = {};
            this.selectedCards = {};
            this.domNode = domConstruct.create('ul', {
                className: 'cards-list clearfix'
            });
        },
        
        addCard: function(card) {
            this.unselect(card);
            this.cards[card.getId()] = card;
            domConstruct.place(card.domNode, this.domNode);
            this.length++;
        },
        
        removeCard: function(cardId) {
            var card = this.cards[cardId];
            if (card) {
                this.unselect(card);
                this.domNode.removeChild(card.domNode);
                delete this.cards[cardId];
                this.length--;
            }
            return card;
        },
        
        revealCards: function() {
            var cards = this.cards;
            for (var id in cards) {
                if (cards.hasOwnProperty(id)) {
                    cards[id].reveal();
                }
            }
        },
        
        clearSelection: function() {
            var selected = this.selectedCards;
            for (var id in selected) {
                if (selected.hasOwnProperty(id)) {
                    this.unselect(selected[id]);
                }
            }
        },
        
        initSelection: function(maxSelected) {
            this.maxSelected = maxSelected;
            
            var self = this;
            on(this.domNode, '.card:click', function() {
                if (self.selectionEnabled) {
                    var card = registry.byNode(this), isSelected = self.isSelected(card);
                    self[isSelected ? 'unselect' : 'select'](card);
                }
            });
        },
        
        enableSelection: function() {
            domClass.add(this.domNode, 'selection-enabled');
            this.selectionEnabled = true;
        },
        
        disableSelection: function() {
            domClass.remove(this.domNode, 'selection-enabled');
            this.selectionEnabled = false;
            this.clearSelection();
        },
        
        isSelected: function(card) {
            return (this.selectedCards[card.getId()] !== undefined);
        },
        
        select: function(card) {
            if (!this.isSelected(card) && this.selected < this.maxSelected) {
                domClass.add(card.domNode, 'card-selected');
                this.selectedCards[card.getId()] = card;
                this.selected++;
                this.emit('Select', {card: card});
            }
        },
        
        unselect: function(card) {
            if (this.isSelected(card)) {
                domClass.remove(card.domNode, 'card-selected');
                delete this.selectedCards[card.getId()];
                this.selected--;
                this.emit('Deselect', {card: card});
            }
        },
        
        onSelect: function(e) {},
        
        onDeselect: function(e) {}

    });

});