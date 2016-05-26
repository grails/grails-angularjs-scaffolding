package grails.plugin.scaffolding.angular.template

import grails.plugin.scaffolding.model.property.DomainProperty
import org.grails.datastore.mapping.model.types.ToMany

class AngularDomainHelper {
    
    List<AngularDomainAssociation> associations
    final String getConfig
    final String queryConfig
    final String moduleConfig

    AngularDomainHelper(List<DomainProperty> associatedProperties) {
        this.associations = associatedProperties.collect { DomainProperty property ->
            new AngularDomainAssociation(property.associatedType.simpleName, property)
        }

        List getTransforms = []
        List queryTransforms = []
        Set moduleInjections = []

        if (associations) {
            moduleInjections.add('domainListConversion')
            getTransforms.add('angular.fromJson')
            queryTransforms.add('angular.fromJson')
            associations.each { AngularDomainAssociation association ->
                String function
                if (association.toMany) {
                    function = "domainToManyConversion"
                } else {
                    function = "domainConversion"
                }
                moduleInjections.add(function)
                queryTransforms.add("domainListConversion(\"${association.className}\", \"${association.propertyName}\", \"$function\")")
                getTransforms.add("$function(\"${association.className}\", \"${association.propertyName}\")")
            }
        }
        
        this.getConfig = getTransforms ? ", transformResponse: [" + getTransforms.join(", ") + "]" : ""
        this.queryConfig = queryTransforms ? ", transformResponse: [" + queryTransforms.join(", ") + "]" : ""
        this.moduleConfig = moduleInjections ? ", " + moduleInjections.join(", ") : ""
    }
    
    private static class AngularDomainAssociation {
        String className
        String propertyName
        Boolean toMany

        AngularDomainAssociation(String className, DomainProperty property) {
            this.className = className
            this.propertyName = property.name
            this.toMany = property.persistentProperty instanceof ToMany
        }
    }
}
