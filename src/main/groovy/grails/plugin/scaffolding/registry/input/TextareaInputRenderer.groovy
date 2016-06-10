package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * The default renderer for rendering properties with the constraint {@code [widget: "textarea"]}
 *
 * @author James Kleeh
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
