package org.grails.plugin.scaffolding.angular.template

import org.grails.scaffolding.model.property.DomainProperty
import grails.web.mapping.UrlMappings
import grails.web.mapping.exceptions.UrlMappingException
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.ToMany

class AngularDomainHelper {
    
    List<AngularDomainAssociation> associations
    final String getConfig
    final String queryConfig
    final String moduleConfig
    final String uri

    AngularDomainHelper(PersistentEntity domain, List<DomainProperty> associatedProperties, UrlMappings urlMappings) {
        this.associations = associatedProperties.collect { DomainProperty property ->
            new AngularDomainAssociation(property.associatedType.simpleName, property)
        }
        String uri
        try {
            uri = urlMappings
                    .getReverseMapping(domain.decapitalizedName, "index", null, null, "GET", Collections.emptyMap())
                    .createRelativeURL(domain.decapitalizedName, "index", [:], 'UTF8')
                    .replaceFirst('/', '')
                    .replace('/index', '')
        } catch (UrlMappingException e) {
            uri = domain.decapitalizedName
        }
        this.uri = uri

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
