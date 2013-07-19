define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/dom-construct',
    'dojo/dom-class',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dijit/_WidgetsInTemplateMixin',
    'dojox/html/entities',
    'gofish/data-service',
    'gofish/widget/PlayerControls',
    'gofish/widget/Card',
    'gofish/widget/CardsList',
    'dojo/text!gofish/template/Player.html'
], function(declare, lang, domConstruct, domClass, _WidgetBase, _TemplatedMixin,
            _WidgetsInTemplateMixin, entities, dataService, PlayerControls, Card,
            CardsList, template) {

    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {

        templateString: template,
        
        playing: true,
        
        controls: null,
        
        numCompleteSeries: 0,

        postCreate: function() {
            this.inherited(arguments);
            this.nameNode.innerHTML = entities.encode(this.playerName);
        },
        
        getId: function() {
            return this.playerId;
        },
        
        getName: function() {
            return this.playerName;
        },
        
        setHand: function(hand) {
            for (var i = 0, l = hand.length; i < l; i++) {
                var card = new Card({
                    cardId: hand[i].id,
                    cardName: hand[i].name
                });
                card.conceal();
                this.hand.addCard(card);
            }
            this.updateCounters();
        },
        
        updateCounters: function() {
            this.numCardsNode.innerHTML = this.hand.length;
            this.numCompleteSeriesNode.innerHTML = this.numCompleteSeries;
        },
        
        removeCard: function(cardId) {
            var card = this.hand.removeCard(cardId);
            this.updateCounters();
            return card;
        },
        
        addCard: function(card) {
            this.hand.addCard(card);
            this.updateCounters();
        },
        
        dropSeries: function(series) {
            var newSeries = this.createSeries(series.cards);
            domConstruct.place(newSeries.domNode, this.completeSeries);
            this.numCompleteSeries++;
            this.updateCounters();
        },
        
        createSeries: function(cards) {
            var series = new CardsList();
            for (var i = 0, l = cards.length; i < l; i++) {
                var card = this.hand.removeCard(cards[i].id);
                card.reveal();
                series.addCard(card);
            }
            return series;
        },
        
        initControls: function(players) {
            this.controls = new PlayerControls(null, this.controlsContainer);
            this.controls.on('askCard', lang.hitch(this, 'askCard'));
            
            for (var playerId in players) {
                if (players.hasOwnProperty(playerId)) {
                    var player = players[playerId];
                    if (player !== this) {
                        this.controls.addPlayer(player);
                    }
                }
            }
            
            this.hand.revealCards();
            this.hand.initSelection();
        },

        setCurrentPlayer: function(flag) {
            domClass[flag ? 'add' : 'remove'](this.domNode, 'current');
            if (this.controls) {
                this.controls.show(flag);
            }
        },
        
        askCard: function(event) {
            dataService.askCard(event.playerId, event.cardName);
        }

    });

});