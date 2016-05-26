package grails.plugin.scaffolding.angular.registry.output

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainOutputRenderer

/**
 * Created by Jim on 5/26/2016.
 */
class AngularIdOutputRenderer extends AngularDomainOutputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        property.domainClass.isIdentityName(property.name)
    }

    @Override
    String getControllerName() {
        return null
    }

    @Override
    Closure renderListOutput(DomainProperty property) {
        final String propertyName = getPropertyName(property)
        return { ->
            a("{{${buildPropertyPath(property, false)}}}", ["ui-sref": "${propertyName}.show({id: ${propertyName}.id})"])
        }
    }

    @Override
    protected Closure renderOutput(String propertyName, String propertyPath) {
        { ->
            span("{{${propertyPath}}}")
        }
    }
}
