package grails.plugin.scaffolding.angular.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.input.BidirectionalToManyInputRenderer
import grails.util.GrailsNameUtils
import org.grails.datastore.mapping.model.types.Association

/**
 * Created by Jim on 5/25/2016.
 */
class AngularBidirectionalToManyInputRenderer extends BidirectionalToManyInputRenderer {

    private String controllerName

    AngularBidirectionalToManyInputRenderer(String controllerName) {
        super(null)
        this.controllerName = controllerName
    }

    protected String getInverseSideName(DomainProperty property) {
        ((Association)property.persistentProperty).inverseSide.name
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        final String stateName = GrailsNameUtils.getPropertyName(property.associatedType)
        final String objectName = "${controllerName}.${getPropertyName(property)}.id"
        return { ->
            a("Add ${getAssociatedClassName(property)}", ["ui-sref": "${stateName}.create({${getInverseSideName(property)}Id: ${objectName}})"])
        }
    }
}
