package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/23/2016.
 */
class StringInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty domainProperty) {
        println domainProperty.persistentProperty
        println domainProperty.type in [String, null]
        domainProperty.type in [String, null]
    }

    @Override
    Closure renderInput(Map standardAttributes, DomainProperty domainProperty) {
        if (domainProperty.constraints?.password) {
            standardAttributes.type = "password"
        } else if (domainProperty.constraints?.email)  {
            standardAttributes.type = "email"
        } else if (domainProperty.constraints?.url) {
            standardAttributes.type = "url"
        } else {
            standardAttributes.type = "text"
        }

        if (domainProperty.constraints?.matches) {
            standardAttributes.pattern = domainProperty.constraints.matches
        }
        if (domainProperty.constraints?.maxSize) {
            standardAttributes.maxlength = domainProperty.constraints.maxSize
        }

        return { ->
            input(standardAttributes)
        }
    }
}
