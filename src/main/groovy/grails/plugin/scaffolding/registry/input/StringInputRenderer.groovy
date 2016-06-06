package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer
import grails.validation.Constrained

/**
 * Created by Jim on 5/23/2016.
 */
class StringInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty domainProperty) {
        domainProperty.type in [String, null]
    }

    @Override
    Closure renderInput(Map standardAttributes, DomainProperty domainProperty) {
        Constrained constraints = domainProperty.constraints
        if (constraints?.password) {
            standardAttributes.type = "password"
        } else if (constraints?.email)  {
            standardAttributes.type = "email"
        } else if (constraints?.url) {
            standardAttributes.type = "url"
        } else {
            standardAttributes.type = "text"
        }

        if (constraints?.matches) {
            standardAttributes.pattern = constraints.matches
        }
        if (constraints?.maxSize) {
            standardAttributes.maxlength = constraints.maxSize
        }

        return { ->
            input(standardAttributes)
        }
    }
}
