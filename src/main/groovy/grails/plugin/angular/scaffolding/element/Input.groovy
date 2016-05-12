package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor

/**
 * Created by Jim on 5/4/2016.
 */
class Input extends Element {

    String type

    Input(BeanPropertyAccessor property, String type) {
        this(property)
        this.type = type
    }

    Input(BeanPropertyAccessor property) {
        super(property)
    }

    @Override
    String render() {
        "<input type=\"${type}\" ${renderOtherAttributes()} />"
    }
}
