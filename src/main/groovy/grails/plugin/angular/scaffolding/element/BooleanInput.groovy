package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor

/**
 * Created by Jim on 5/4/2016.
 */
class BooleanInput extends Input {

    BooleanInput(BeanPropertyAccessor property) {
        super(property)
        type = "checkbox"
    }

}
