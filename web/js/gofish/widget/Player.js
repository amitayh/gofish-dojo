define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/_base/fx',
    'dojo/dom-construct',
    'dojo/dom-class',
    'dojo/dom-style',
    'dojo/Deferred',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dijit/_WidgetsInTemplateMixin',
    'dojox/html/entities',
    'gofish/data-service',
    'gofish/widget/PlayerControls',
    'gofish/widget/Card',
    'gofish/widget/CardsList',
    'gofish/widget/Balloon',
    'dojo/text!gofish/template/Player.html'
], function(declare, lang, fx, domConstruct, domClass, domStyle, Deferred, _WidgetBase,
            _TemplatedMixin, _WidgetsInTemplateMixin, entities, dataService,
            PlayerControls, Card, CardsList, Balloon, template) {
    
    var MaxSelectedCards = 4,
        DefaultBalloonDelay = 1500;

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
        
        say: function(message, delay) {
            var deferred = new Deferred(),
                balloon = this.balloon;
            
            balloon.setContent(message);
            balloon.show(this.nameNode);
            setTimeout(function() {
                balloon.hide();
                deferred.resolve();
            }, delay || DefaultBalloonDelay);
            
            return deferred.promise;
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
        
        animateAddCard: function(card, startPosition) {
            // Init
            domStyle.set(card.domNode, {
                position: 'absolute',
                top: startPosition.y + 'px',
                left: startPosition.x + 'px'
            });
            
            // Add card
            var deferred = new Deferred(),
                endPosition = this.hand.getNextCardPosition();
            
            this.addCard(card);
            
            // Animate position
            fx.animateProperty({
                node: card.domNode,
                properties: {
                    top: endPosition.y,
                    left: endPosition.x
                },
                onEnd: function() {
                    domStyle.set(card.domNode, 'position', 'static');
                    deferred.resolve();
                }
            }).play();
            
            // Promise will be resolved when animation is finished
            return deferred.promise;
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
            this.controls.on('dropSeriesClick', lang.hitch(this, 'dropSeriesClick'));
            this.controls.on('skipTurnClick', lang.hitch(this, 'skipTurnClick'));
            this.controls.on('quitGameClick', lang.hitch(this, 'quitGameClick'));
            this.addPlayers(players);
            
            this.hand.initSelection(MaxSelectedCards);
            var checkDropSeriesButton = lang.hitch(this, 'checkDropSeriesButton');
            this.hand.on('select', checkDropSeriesButton);
            this.hand.on('deselect', checkDropSeriesButton);
            this.hand.revealCards();
        },
        
        addPlayers: function(players) {
            for (var playerId in players) {
                if (players.hasOwnProperty(playerId)) {
                    var player = players[playerId];
                    if (player !== this) {
                        this.controls.addPlayer(player);
                    }
                }
            }
        },
        
        checkDropSeriesButton: function() {
            var disabled = this.hand.selected < MaxSelectedCards;
            this.controls.dropSeriesButton.disabled = disabled;
        },

        setCurrentPlayer: function(flag) {
            domClass[flag ? 'add' : 'remove'](this.domNode, 'current');
            if (this.controls) {
                this.controls.show(flag);
            }
        },
        
        setError: function(message) {
            alert(message); // TODO
        },
        
        clearError: function() {
            // TODO
        },
        
        quitGame: function() {
            this.playing = false;
            domClass.add(this.domNode, 'muted');
        },
        
        askCard: function(event) {
            var self = this;
            dataService.askCard(event.playerId, event.cardName).then(function(response) {
                self.clearError();
                if (!response.success) {
                    self.setError(response.message);
                }
            });
        },
        
        dropSeriesClick: function() {
            var self = this;
            dataService.dropSeries(this.hand.selectedCards).then(function(response) {
                self.hand.clearSelection();
                self.clearError();
                if (!response.success) {
                    self.setError(response.message);
                }
            });
        },
        
        skipTurnClick: function() {
            dataService.skipTurn();
        },
        
        quitGameClick: function() {
            dataService.quitGame();
        }

    });

});