package grails.plugin.scaffolding.model

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.model.property.DomainPropertyFactory
import grails.util.GrailsClassUtils
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Embedded
import org.springframework.beans.factory.annotation.Autowired

class DomainModelServiceImpl implements DomainModelService {

    @Autowired
    DomainPropertyFactory domainPropertyFactory

    List<DomainProperty> getEditableProperties(PersistentEntity domainClass) {
        List<DomainProperty> properties = domainClass.persistentProperties.collect {
            domainPropertyFactory.build(it)
        }
        List blacklist = ['version', 'dateCreated', 'lastUpdated']
        def scaffoldProp = GrailsClassUtils.getStaticPropertyValue(domainClass.javaClass, 'scaffold')
        if (scaffoldProp) {
            blacklist.addAll(scaffoldProp.exclude)
        }
        properties.removeAll { it.name in blacklist }
        properties.removeAll { !it.constraints.display }
        //TODO Wait for Graeme to implement access to determine if a property is derived
        //properties.removeAll { it.mapping instanceof PropertyConfig classMapping.mappedForm..properties.derived }
        properties.sort()
        properties
    }

    List<DomainProperty> getVisibleProperties(PersistentEntity domainClass) {
        List<DomainProperty> properties = domainClass.persistentProperties.collect {
            domainPropertyFactory.build(it)
        }
        properties.removeAll { it.name == 'version' }
        properties.sort()
        properties
    }

    List<PersistentProperty> getShortListVisibleProperties(PersistentEntity domainClass) {
        List<PersistentProperty> properties = getVisibleProperties(domainClass)
        if (properties.size() > 5) {
            properties = properties[0..5]
        }
        properties.add(0, domainPropertyFactory.build(domainClass.identity))
        properties
    }

    List<DomainProperty> findEditableProperties(PersistentEntity domainClass, Closure closure) {
        List<DomainProperty> properties = []
        getEditableProperties(domainClass).each { DomainProperty domainProperty ->
            PersistentProperty property = domainProperty.persistentProperty
            if (property instanceof Embedded) {
                getEditableProperties(property.associatedEntity).each { DomainProperty embedded ->
                    embedded.rootProperty = domainProperty
                    if (closure.call(embedded)) {
                        properties.add(embedded)
                    }
                }
            } else {
                if (closure.call(domainProperty)) {
                    properties.add(domainProperty)
                }
            }
        }
        properties
    }

    List<DomainProperty> findProperties(List<DomainProperty> propertyList, Closure closure) {
        List<DomainProperty> properties = []
        propertyList.each { DomainProperty domainProperty ->
            PersistentProperty property = domainProperty.persistentProperty
            if (property instanceof Embedded) {
                getEditableProperties(property.associatedEntity).each { DomainProperty embedded ->
                    embedded.rootProperty = domainProperty
                    if (closure.call(embedded)) {
                        properties.add(embedded)
                    }
                }
            } else {
                if (closure.call(domainProperty)) {
                    properties.add(domainProperty)
                }
            }
        }
        properties
    }

    Boolean hasEditableProperty(PersistentEntity domainClass, Closure closure) {
        findEditableProperties(domainClass, closure).size() > 0
    }

}
