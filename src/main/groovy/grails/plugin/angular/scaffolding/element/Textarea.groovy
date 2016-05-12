package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor

/**
 * Created by Jim on 5/4/2016.
 */
class Textarea extends StringInput {

    Textarea(BeanPropertyAccessor property) {
        super(property)
    }

    @Override
    String render() {
        "<textarea ${renderOtherAttributes()}></textarea>"
    }
}
