package grails.plugin.scaffolding.markup

import grails.plugin.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/14/2016.
 */
trait PropertyMarkupRenderer {

    Map getStandardAttributes(DomainProperty property) {
        final String name = property.pathFromRoot
        Map attributes = [:]
        if (property.required) {
            attributes.required = null
        }
        if (property.constraints && !property.constraints.editable) {
            attributes.readonly = null
        }
        attributes.name = name
        attributes.id = name
        attributes
    }

    abstract Closure renderListOutput(DomainProperty property)

    abstract Closure renderOutput(DomainProperty property)

    abstract Closure renderInput(DomainProperty property)
}