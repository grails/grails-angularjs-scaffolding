package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/24/2016.
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
