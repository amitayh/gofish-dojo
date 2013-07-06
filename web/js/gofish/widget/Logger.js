define([
    'dojo/_base/declare',
    'dojo/dom-construct',
    'dojo/date/locale',
    'dijit/_WidgetBase'
], function(declare, domConstruct, locale, _WidgetBase) {
    
    return declare(_WidgetBase, {

        timestampOptions: {selector: 'time', timePattern: 'HH:mm'},

        buildRendering: function() {
            this.domNode = domConstruct.create('ul', {className: 'logger'});
        },

        log: function(message) {
            var entry = this.createLogEntry(message);
            domConstruct.place(entry, this.domNode);
            this.scrollToBottom();
        },

        createLogEntry: function (message) {
            var html = this.getTimestamp() + ' ' + message;
            return domConstruct.create('li', {innerHTML: html});
        },

        getTimestamp: function() {
            var now = locale.format(new Date(), this.timestampOptions);
            return '<span class="muted">[' + now + ']</span>';
        },

        scrollToBottom: function() {
            this.domNode.scrollTop = this.domNode.scrollHeight;
        }
        
    });
    
});