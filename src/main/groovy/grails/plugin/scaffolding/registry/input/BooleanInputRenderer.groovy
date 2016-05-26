package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/23/2016.
 */
class BooleanInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty domainProperty) {
        domainProperty.type in [boolean, Boolean]
    }

    @Override
    Closure renderInput(Map standardAttributes, DomainProperty domainProperty) {
        standardAttributes.type = "checkbox"
        return { ->
            input(standardAttributes)
        }
    }
}
