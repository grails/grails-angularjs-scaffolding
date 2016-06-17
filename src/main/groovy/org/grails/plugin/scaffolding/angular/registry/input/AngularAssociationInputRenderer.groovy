package org.grails.plugin.scaffolding.angular.registry.input

import grails.util.GrailsNameUtils
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.ToMany
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.input.AssociationInputRenderer

/**
 * Created by Jim on 5/25/2016.
 */
class AngularAssociationInputRenderer extends AssociationInputRenderer {

    private String controllerName

    AngularAssociationInputRenderer(String controllerName) {
        this.controllerName = controllerName
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {

        final String name = GrailsNameUtils.getPropertyName(property.associatedType)
        final String identityName = property.associatedEntity.identity.name
        defaultAttributes['ng-options'] = "$name for $name in ${controllerName}.${name}List track by ${name}.${identityName}"

        PersistentProperty persistentProperty = property.persistentProperty
        if (persistentProperty instanceof ToMany && !persistentProperty.bidirectional) {
            defaultAttributes["multiple"] = ""
        }

        return { ->
            select('', defaultAttributes)
        }
    }
}
