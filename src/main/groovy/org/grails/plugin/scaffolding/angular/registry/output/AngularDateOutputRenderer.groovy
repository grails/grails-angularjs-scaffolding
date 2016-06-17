package org.grails.plugin.scaffolding.angular.registry.output

import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.input.DateInputRenderer

/**
 * Created by Jim on 5/25/2016.
 */
class AngularDateOutputRenderer extends AngularDomainOutputRenderer {

    final String controllerName

    AngularDateOutputRenderer(String controllerName) {
        this.controllerName = controllerName
    }

    @Override
    boolean supports(DomainProperty property) {
        property.type in [Date, Calendar, java.sql.Date]
    }

    @Override
    protected Closure renderOutput(String propertyName, String propertyPath) {
        { ->
            span("{{${propertyPath} | date:'yyyy-MM-dd HH:mm:ss Z'}}")
        }
    }
}
