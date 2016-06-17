package org.grails.plugin.scaffolding.angular.registry.output

import org.grails.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/26/2016.
 */
class AngularIdOutputRenderer extends AngularDomainOutputRenderer {


    final String controllerName

    AngularIdOutputRenderer(String controllerName) {
        this.controllerName = controllerName
    }

    @Override
    boolean supports(DomainProperty property) {
        property.domainClass.isIdentityName(property.name)
    }

    @Override
    Closure renderListOutput(DomainProperty property) {
        final String propertyPath = buildPropertyPath(property, false)
        return { ->
            a("{{${propertyPath}}}", ["ui-sref": "${getPropertyName(property)}.show({id: ${propertyPath}})"])
        }
    }

    @Override
    protected Closure renderOutput(String propertyName, String propertyPath) {
        { ->
            span("{{${propertyPath}}}")
        }
    }
}
