package grails.plugin.scaffolding.angular.registry.output

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.util.GrailsNameUtils
import org.grails.datastore.mapping.model.types.ToMany

/**
 * Created by Jim on 5/25/2016.
 */
class AngularToManyOutputRenderer extends AngularDomainOutputRenderer {

    final String controllerName

    AngularToManyOutputRenderer(String controllerName) {
        this.controllerName = controllerName
    }

    @Override
    boolean supports(DomainProperty property) {
        property.persistentProperty instanceof ToMany
    }

    protected String getPropertyName(DomainProperty property) {
        GrailsNameUtils.getPropertyName(property.associatedType)
    }

    protected Closure renderOutput(String propertyName, String propertyPath) {
        return { ->
            ul {
                li(['ng-repeat': "${propertyName} in ${propertyPath}"]) {
                    a("{{${propertyName}.toString()}}", ["ui-sref": "${propertyName}.show({id: ${propertyName}.id})"])
                }
            }
        }
    }
}
