package grails.plugin.angular.scaffolding.model

import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.element.PropertyType
import grails.plugin.formfields.BeanPropertyAccessor

/**
 * Created by Jim on 5/12/2016.
 */
interface DomainModelService {

    List<GrailsDomainClassProperty> getEditableProperties(GrailsDomainClass domainClass)

    List<GrailsDomainClassProperty> getVisibleProperties(GrailsDomainClass domainClass)

    void sort(List<GrailsDomainClassProperty> properties, GrailsDomainClass domainClass)

    PropertyType getPropertyType(BeanPropertyAccessor property)

    PropertyType getPropertyType(GrailsDomainClassProperty property)

    Boolean hasFileProperty(GrailsDomainClass domainClass)

    Boolean hasTimeZoneProperty(GrailsDomainClass domainClass)

    Map<String, String> getTimeZones()

}
