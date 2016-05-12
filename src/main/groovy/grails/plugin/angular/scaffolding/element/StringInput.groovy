package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor
import grails.validation.ConstrainedProperty

/**
 * Created by Jim on 5/4/2016.
 */
class StringInput extends Input {

    Element otherElement

    StringInput(BeanPropertyAccessor property) {
        super(property)
        ConstrainedProperty constraint = property.constraints
        if (constraint?.inList) {
            otherElement = new Select(property)
        } else {
            if (property.constraints?.password) {
                type = "password"
            } else if (property.constraints?.email)  {
                type = "email"
            } else if (property.constraints?.url) {
                type = "url"
            } else {
                type = "text"
            }

            if (property.constraints?.matches) {
                otherAttributes.pattern = property.constraints.matches
            }
            if (property.constraints?.maxSize) {
                otherAttributes.maxlength = property.constraints.maxSize
            }
        }

        if (property.constraints?.widget == "textarea") {
            otherElement = new Textarea(property)
        }
    }

    @Override
    String render() {
        if (otherElement) {
            otherElement.render()
        } else {
            super.render()
        }
    }

}
