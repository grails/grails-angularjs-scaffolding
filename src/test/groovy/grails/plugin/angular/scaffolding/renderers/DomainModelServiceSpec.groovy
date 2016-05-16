package grails.plugin.angular.scaffolding.renderers

import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.model.DomainModelServiceImpl
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Jim on 5/15/2016.
 */
class DomainModelServiceSpec extends Specification {

    @Shared
    DomainModelService domainModelService

    void setup() {
        domainModelService = new DomainModelServiceImpl()
    }

    void "test getEditableProperties valid property"() {
        given:
        GrailsDomainClass domainClass = Mock {
            1 * getClazz() >> ScaffoldedDomain
        }
        GrailsDomainClassProperty bar = Mock {
            2 * getName() >> "bar"
            1 * getDomainClass() >> domainClass
            1 * isDerived() >> false
        }
        1 * domainClass.getPersistentProperties() >> [bar]
        domainClass.getConstrainedProperties() >> ["bar": [display: true]]

        when:
        List<GrailsDomainClassProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.size() == 1
        properties[0] == bar
    }

    void "test getEditableProperties derived"() {
        given:
        GrailsDomainClass domainClass = Mock {
            1 * getClazz() >> ScaffoldedDomain
        }
        GrailsDomainClassProperty bar = Mock {
            2 * getName() >> "bar"
            1 * getDomainClass() >> domainClass
            1 * isDerived() >> true
        }
        1 * domainClass.getPersistentProperties() >> [bar]
        domainClass.getConstrainedProperties() >> ["bar": [display: true]]

        when:
        List<GrailsDomainClassProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    void "test getEditableProperties dateCreated"() {
        given:
        GrailsDomainClass domainClass = Mock {
            1 * getClazz() >> ScaffoldedDomain
        }
        GrailsDomainClassProperty dateCreated = Mock {
            1 * getName() >> "dateCreated"
            0 * getDomainClass()
            0 * isDerived()
        }
        1 * domainClass.getPersistentProperties() >> [dateCreated]
        domainClass.getConstrainedProperties() >> ["dateCreated": [display: true]]

        when:
        List<GrailsDomainClassProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    void "test getEditableProperties lastUpdated"() {
        given:
        GrailsDomainClass domainClass = Mock {
            1 * getClazz() >> ScaffoldedDomain
        }
        GrailsDomainClassProperty lastUpdated = Mock {
            1 * getName() >> "lastUpdated"
            0 * getDomainClass()
            0 * isDerived()
        }
        1 * domainClass.getPersistentProperties() >> [lastUpdated]
        domainClass.getConstrainedProperties() >> ["lastUpdated": [display: true]]

        when:
        List<GrailsDomainClassProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    void "test getEditableProperties constraints display false"() {
        given:
        GrailsDomainClass domainClass = Mock {
            1 * getClazz() >> ScaffoldedDomain
        }
        GrailsDomainClassProperty bar = Mock {
            2 * getName() >> "bar"
            1 * getDomainClass() >> domainClass
        }
        1 * domainClass.getPersistentProperties() >> [bar]
        domainClass.getConstrainedProperties() >> ["bar": [display: false]]

        when:
        List<GrailsDomainClassProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    void "test getEditableProperties scaffold exclude"() {
        given:
        GrailsDomainClass domainClass = Mock {
            1 * getClazz() >> ScaffoldedDomain
        }
        GrailsDomainClassProperty foo = Mock {
            1 * getName() >> "foo"
        }
        1 * domainClass.getPersistentProperties() >> [foo]

        when:
        List<GrailsDomainClassProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    class ScaffoldedDomain {
        static scaffold = [exclude: 'foo']
    }
}
