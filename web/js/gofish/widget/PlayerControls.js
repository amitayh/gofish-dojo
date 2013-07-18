define([
    'dojo/_base/declare',
    'dojo/dom-construct',
    'dojo/dom-class',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojox/html/entities',
    'gofish/data-service',
    'dojo/text!gofish/template/PlayerControls.html'
], function(declare, domConstruct, domClass, _WidgetBase, _TemplatedMixin,
            entities, dataService, template) {

    return declare([_WidgetBase, _TemplatedMixin], {

        templateString: template,
        
        playersList: [],
        
        show: function(flag) {
            domClass[flag ? 'remove' : 'add'](this.domNode, 'hide');
            this.updatePlayerSelector();
            this.updateCardSelector();
        },
        
        addPlayer: function(player) {
            var options = {value: player.getId(), innerHTML: entities.encode(player.getName())},
                option = domConstruct.create('option', options, this.playerSelector);
            
            this.playersList.push({player: player, option: option});
        },
        
        updatePlayerSelector: function() {
            var list = this.playersList, entry;
            for (var i = 0, l = list.length; i < l; i++) {
                entry = list[i];
                if (!entry.player.playing) {
                    entry.option.disabled = true;
                }
            }
        },
        
        updateCardSelector: function() {
            var selector = this.cardSelector;
            domConstruct.empty(selector);
            dataService.getAvailableCards().then(function(cards) {
                for (var i = 0, l = cards.length; i < l; i++) {
                    var options = {innerHTML: entities.encode(cards[i])};
                    domConstruct.create('option', options, selector);
                }
            });
        },
        
        askCardClick: function() {
            this.emit('AskCard', {
                playerId: parseInt(this.playerSelector.value),
                cardName: this.cardSelector.value
            });
        },
        
        onAskCard: function(e) {}
        
    });
    
});