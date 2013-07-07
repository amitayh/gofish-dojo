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

        constructor: function(data) {
            this.inherited(arguments);
            this.data = data;
        },

        postCreate: function() {
            this.inherited(arguments);
            this.name.innerHTML = this.getName(true);
        },

        getId: function() {
            return this.data.id;
        },

        getName: function(safe) {
            var name = this.data.name;
            return safe ? entities.encode(name) : name;
        },

        setCurrentPlayer: function(flag) {
            domClass[flag ? 'add' : 'remove'](this.domNode, 'current');
        }

    });

});