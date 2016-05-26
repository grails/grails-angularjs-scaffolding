package grails.plugin.scaffolding.angular.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.input.AssociationInputRenderer
import grails.util.GrailsNameUtils
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.ToMany

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
        defaultAttributes['ng-options'] = "$name for $name in ${controllerName}.${name}List track by ${name}.id"

        PersistentProperty persistentProperty = property.persistentProperty
        if (persistentProperty instanceof ToMany && !persistentProperty.bidirectional) {
            defaultAttributes["multiple"] = ""
        }

        return { ->
            select('', defaultAttributes)
        }

    }
}
