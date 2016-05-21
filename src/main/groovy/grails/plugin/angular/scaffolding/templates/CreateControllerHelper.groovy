package grails.plugin.angular.scaffolding.templates

import grails.plugin.angular.scaffolding.model.property.DomainProperty
import groovy.json.JsonOutput
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.ManyToOne

/**
 * Created by Jim on 5/21/2016.
 */
class CreateControllerHelper {

    final String stateParams
    final List<CreateControllerParameter> controllerStatements

    CreateControllerHelper(List<DomainProperty> associatedProperties) {
        controllerStatements = associatedProperties.findAll { DomainProperty property ->
   /*         PersistentProperty prop = property.persistentProperty
            prop instanceof ManyToOne && prop.bidirectional*/
            property.persistentProperty instanceof ManyToOne
        }.collect { DomainProperty property ->
            new CreateControllerParameter(property.associatedEntity, property.name)
        }
        if (controllerStatements) {
            stateParams = "\n            params: ${JsonOutput.toJson(controllerStatements.collectEntries { [(it.parameterName): null]})},"
        } else {
            stateParams = ""
        }
    }

    private static class CreateControllerParameter {
        final String className
        final String parameterName
        final String propertyName

        CreateControllerParameter(PersistentEntity persistentEntity, String propertyName) {
            this.propertyName = propertyName
            this.className = persistentEntity.javaClass.simpleName
            this.parameterName = propertyName + "Id"
        }
    }
}
