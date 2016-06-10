package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * The default renderer for rendering {@link URL} properties
 *
 * @author James Kleeh
 */
class UrlInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        property.type in URL
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        defaultAttributes.type = "url"
        return { ->
            input(defaultAttributes)
        }
    }
}
