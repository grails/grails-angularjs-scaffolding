package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * The default renderer for rendering date properties
 *
 * @author James Kleeh
 */
class DateInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        property.type in [Date, Calendar, java.sql.Date]
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        defaultAttributes.type = "date"
        return { ->
            input(defaultAttributes)
        }
    }
}
