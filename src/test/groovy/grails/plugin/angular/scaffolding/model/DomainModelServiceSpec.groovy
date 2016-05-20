package grails.plugin.angular.scaffolding.model

import com.sun.java.swing.plaf.windows.TMSchema
import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.element.PropertyType
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.model.DomainModelServiceImpl
import grails.validation.ConstrainedProperty
import org.grails.core.DefaultGrailsDomainClass
import spock.lang.Shared
import spock.lang.Specification

import java.sql.Time

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
            1 * isDerived() >> false
        }
        1 * domainClass.getPersistentProperties() >> [bar]
        domainClass.getConstrainedProperties() >> ["bar": Mock(ConstrainedProperty) { 1 * isDisplay() >> true }]

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
            1 * isDerived() >> true
        }
        1 * domainClass.getPersistentProperties() >> [bar]
        domainClass.getConstrainedProperties() >> ["bar": Mock(ConstrainedProperty) { 1 * isDisplay() >> true }]

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
            0 * isDerived()
        }
        1 * domainClass.getPersistentProperties() >> [dateCreated]

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
            0 * isDerived()
        }
        1 * domainClass.getPersistentProperties() >> [lastUpdated]

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
        }
        1 * domainClass.getPersistentProperties() >> [bar]
        domainClass.getConstrainedProperties() >> ["bar": Mock(ConstrainedProperty) { 1 * isDisplay() >> false }]

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

    void "test hasPropertyType"() {
        when:
        Boolean hasType = domainModelService.hasPropertyType(new DefaultGrailsDomainClass(ScaffoldedDomain, [:]), propertyType)

        then:
        hasType == expected

        where:
        propertyType             | expected
        PropertyType.TIMEZONE    | true //will search embedded properties
        PropertyType.LOCALE      | true
        PropertyType.STRING      | false
        PropertyType.FILE        | true
        PropertyType.DATE        | true
        PropertyType.CURRENCY    | false
        PropertyType.URL         | false
        PropertyType.ASSOCIATION | false
    }

    void "test getTimeZones"() {
        given:
        Map<String, String> timeZones = domainModelService.timeZones

        expect:
        timeZones["America/New_York"] == "EDT, Eastern Daylight Time -5:0.0 [America/New_York]"
    }

    void "test getLocales"() {
        given:
        Map<String, String> locales = domainModelService.locales

        expect:
        locales["en_US"] == "en, US,  English (United States)"
    }


    class ScaffoldedDomain {
        Long id
        Long version
        static scaffold = [exclude: 'foo']

        EmbeddedAssociate embeddedAssociate
        Locale locale
        byte[] data

        static embedded = ['embeddedAssociate']
    }

    class EmbeddedAssociate {
        TimeZone timeZone
        Calendar cal
    }
}
