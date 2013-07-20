define([
    'dojo/_base/declare',
    'dojo/dom-geometry',
    'dojo/dom-style',
    'dijit/_WidgetBase',
    'dijit/_TemplatedMixin',
    'dojo/text!gofish/template/Balloon.html'
], function(declare, domGeometry, domStyle, _WidgetBase, _TemplatedMixin, template) {
    
    return declare([_WidgetBase, _TemplatedMixin], {
        
        templateString: template,

        setContent: function(content) {
            this.content.innerHTML = content;
        },
        
        show: function(refNode) {
            domStyle.set(this.domNode, {
                display: 'block',
                top: '-1000px',
                left: '-1000px'
            });
            
            var refNodePosition = domGeometry.position(refNode, true),
                balloonPosition = domGeometry.position(this.domNode, true),
                top = refNodePosition.y + (refNodePosition.h / 2) - (balloonPosition.h / 2),
                left = refNodePosition.x + refNodePosition.w + 10;
            
            domStyle.set(this.domNode, {
                top: top + 'px',
                left: left + 'px'
            });
        },
        
        hide: function() {
            domStyle.set(this.domNode, 'display', 'none');
        }

    });

});