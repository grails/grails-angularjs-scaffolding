package grails.plugin.scaffolding.model

import grails.plugin.scaffolding.model.property.DomainProperty
import org.grails.datastore.mapping.model.PersistentEntity


interface DomainModelService {

    List<DomainProperty> getEditableProperties(PersistentEntity domainClass)

    List<DomainProperty> getVisibleProperties(PersistentEntity domainClass)

    List<DomainProperty> getShortListVisibleProperties(PersistentEntity domainClass)

    List<DomainProperty> findEditableProperties(PersistentEntity domainClass, Closure closure)

    Boolean hasProperty(PersistentEntity domainClass, Closure closure)

}
