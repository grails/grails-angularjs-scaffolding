package grails.plugin.angular.scaffolding.model

import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.util.GrailsClassUtils
import org.grails.validation.DomainClassPropertyComparator


/**
 * Created by Jim on 5/9/2016.
 */
class DomainModelServiceImpl implements DomainModelService {

    List<GrailsDomainClassProperty> getEditableProperties(GrailsDomainClass domainClass) {
        List<GrailsDomainClassProperty> properties = domainClass.persistentProperties as List
        List blacklist = ['dateCreated', 'lastUpdated']
        def scaffoldProp = GrailsClassUtils.getStaticPropertyValue(domainClass.clazz, 'scaffold')
        if (scaffoldProp) {
            blacklist.addAll(scaffoldProp.exclude)
        }
        properties.removeAll { it.name in blacklist }
        properties.removeAll { !it.domainClass.constrainedProperties[it.name]?.display }
        properties.removeAll { it.derived }

        sort(properties, domainClass)
        properties
    }

    List<GrailsDomainClassProperty> getVisibleProperties(GrailsDomainClass domainClass) {
        List<GrailsDomainClassProperty> properties = domainClass.persistentProperties
        sort(properties, domainClass)
        if (properties.size() > 6) {
            properties = properties[0..6]
        }
        properties
    }

    static void sort(List<GrailsDomainClassProperty> properties, GrailsDomainClass domainClass) {
        Collections.sort(properties, new DomainClassPropertyComparator(domainClass))
    }


}
