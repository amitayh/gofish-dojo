define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/dom-class',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dijit/_WidgetsInTemplateMixin',
    'dojox/html/entities',
    'gofish/data-service',
    'gofish/widget/PlayerControls',
    'gofish/widget/CardsList',
    'dojo/text!gofish/template/Player.html'
], function(declare, lang, domClass, _WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin,
            entities, dataService, PlayerControls, CardsList, template) {

    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin], {

        templateString: template,
        
        playing: true,
        
        controls: null,

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
            
            this.handWidget.revealCards();
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