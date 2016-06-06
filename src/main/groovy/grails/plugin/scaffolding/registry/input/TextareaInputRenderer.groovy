package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/23/2016.
 */
class TextareaInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty domainProperty) {
        domainProperty.constraints?.widget == "textarea"
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty domainProperty) {
        Integer maxSize = domainProperty.constraints?.maxSize
        if (maxSize) {
            defaultAttributes.maxlength = maxSize
        }
        return { ->
            textarea(defaultAttributes)
        }
    }
}
