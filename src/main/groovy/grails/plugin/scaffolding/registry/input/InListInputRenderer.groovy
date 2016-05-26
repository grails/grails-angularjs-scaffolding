package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/23/2016.
 */
class InListInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty domainProperty) {
        domainProperty.constraints?.inList
    }

    @Override
    Closure renderInput(Map standardAttributes, DomainProperty domainProperty) {
        List inList = domainProperty.constraints?.inList

        return { ->
            select(standardAttributes) {
                inList.each {
                    option(it, [value: it])
                }
            }
        }
    }
}
