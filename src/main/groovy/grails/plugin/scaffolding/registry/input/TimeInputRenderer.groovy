package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * The default renderer for rendering {@link java.sql.Time} properties
 *
 * @author James Kleeh
 */
class TimeInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        property.type in java.sql.Time
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        defaultAttributes.type = "datetime-local"
        return { ->
            input(defaultAttributes)
        }
    }
}
