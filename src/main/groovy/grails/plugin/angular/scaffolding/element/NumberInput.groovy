package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor
import grails.validation.ConstrainedProperty

/**
 * Created by Jim on 5/4/2016.
 */
class NumberInput extends Input {

    Select selectElement

    private static final List<String> DECIMAL_TYPES = ['double', 'float', 'bigdecimal']

    NumberInput(BeanPropertyAccessor property) {
        super(property)
        ConstrainedProperty constraint = property.constraints
        if (constraint?.inList) {
            selectElement = new Select(property)
        } else {
            if (property.constraints?.range) {
                type = "range"
                otherAttributes.min = property.constraints.range.from
                otherAttributes.max = property.constraints.range.to
            } else {
                String typeName = property.propertyType.simpleName.toLowerCase()

                if(typeName in DECIMAL_TYPES) {
                    type = "number decimal"
                } else {
                    type = "number"
                }
                if (property.constraints?.scale != null) {
                    otherAttributes.step = "0.${'0' * (property.constraints.scale - 1)}1"
                }
                if (property.constraints?.min != null) {
                    otherAttributes.min = property.constraints.min
                }
                if (property.constraints?.max != null) {
                    otherAttributes.max = property.constraints.max
                }
            }
        }
    }

    @Override
    String render() {
        if (selectElement) {
            selectElement.render()
        } else {
            super.render()
        }
    }
}
