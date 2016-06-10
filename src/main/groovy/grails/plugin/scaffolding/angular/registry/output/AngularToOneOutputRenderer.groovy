package grails.plugin.scaffolding.angular.registry.output

import org.grails.scaffolding.model.property.DomainProperty
import grails.util.GrailsNameUtils
import org.grails.datastore.mapping.model.types.ToOne

/**
 * Created by Jim on 5/25/2016.
 */
class AngularToOneOutputRenderer extends AngularDomainOutputRenderer {

    final String controllerName

    AngularToOneOutputRenderer(String controllerName) {
        this.controllerName = controllerName
    }

    @Override
    boolean supports(DomainProperty property) {
        property.persistentProperty instanceof ToOne
    }

    protected String getPropertyName(DomainProperty property) {
        GrailsNameUtils.getPropertyName(property.associatedType)
    }

    protected Closure renderOutput(String propertyName, String propertyPath) {
        return { ->
            a("{{${propertyPath}.toString()}}", ["ui-sref": "${propertyName}.show({id: ${propertyPath}.id})"])
        }
    }
}
