package grails.plugin.angular.scaffolding.model

import grails.plugin.angular.scaffolding.model.property.PropertyType
import grails.plugin.angular.scaffolding.model.property.DomainProperty
import grails.plugin.angular.scaffolding.model.property.DomainPropertyFactory
import org.grails.datastore.mapping.model.PersistentEntity

/**
 * Created by Jim on 5/12/2016.
 */
interface DomainModelService {

    List<DomainProperty> getEditableProperties(PersistentEntity domainClass)

    List<DomainProperty> getVisibleProperties(PersistentEntity domainClass)

    List<DomainProperty> getShortListVisibleProperties(PersistentEntity domainClass)

    PropertyType getPropertyType(DomainProperty property)

    Boolean hasPropertyType(PersistentEntity domainClass, PropertyType propertyType)

    DomainPropertyFactory getDomainPropertyFactory()

    void setDomainPropertyFactory(DomainPropertyFactory domainPropertyFactory)

    Map<String, String> getTimeZones()

    Map<String, String> getLocales()

    List<String> getCurrencyCodes()

    List<String> getDecimalTypes()

}
