define([
    'dojo/_base/declare',
    'dojo/dom-construct',
    'dojo/date/locale',
    'dijit/_WidgetBase'
], function(declare, domConstruct, locale, _WidgetBase) {

    return declare(_WidgetBase, {

        buildRendering: function() {
            this.domNode = domConstruct.create('ul', {className: 'gofish-cards'});
        }

    });

});