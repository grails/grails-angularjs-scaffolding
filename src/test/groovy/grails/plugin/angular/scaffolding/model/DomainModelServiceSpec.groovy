package grails.plugin.angular.scaffolding.model

import grails.core.GrailsDomainClass
import grails.plugin.angular.scaffolding.model.property.PropertyType
import grails.plugin.angular.scaffolding.model.property.DomainProperty
import grails.plugin.angular.scaffolding.model.property.DomainPropertyFactory
import grails.plugin.angular.scaffolding.model.property.DomainPropertyFactoryImpl
import grails.validation.ConstrainedProperty
import org.grails.core.DefaultGrailsDomainClass
import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.validation.GrailsDomainClassValidator
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


class DomainModelServiceSpec extends Specification {

    @Shared
    DomainModelService domainModelService

    @Shared
    PersistentEntity domainClass

    void setup() {
        domainModelService = new DomainModelServiceImpl()
        domainClass = Mock(PersistentEntity) {
            (0..1) * getJavaClass() >> ScaffoldedDomain
        }
    }

    void "test getEditableProperties valid property"() {
        given:
        PersistentProperty bar = Mock()
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getConstraints() >> Mock(ConstrainedProperty) { 1 * isDisplay() >> true }
            1 * getName() >> "bar"
        }
        domainModelService.domainPropertyFactory = Mock(DomainPropertyFactoryImpl) {
            1 * build(bar) >> domainProperty
        }
        1 * domainClass.getPersistentProperties() >> [bar]

        when:
        List<DomainProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.size() == 1
        properties[0] == domainProperty
    }

    /*
    TODO: Wait until derived is added to next version of GORM
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
    */

    void "test getEditableProperties dateCreated"() {
        given:
        PersistentProperty dateCreated = Mock()
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getName() >> "dateCreated"
        }
        domainModelService.domainPropertyFactory = Mock(DomainPropertyFactoryImpl) {
            1 * build(dateCreated) >> domainProperty
        }
        1 * domainClass.getPersistentProperties() >> [dateCreated]

        when:
        List<DomainProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    void "test getEditableProperties lastUpdated"() {
        given:
        PersistentProperty lastUpdated = Mock()
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getName() >> "lastUpdated"
        }
        domainModelService.domainPropertyFactory = Mock(DomainPropertyFactoryImpl) {
            1 * build(lastUpdated) >> domainProperty
        }
        1 * domainClass.getPersistentProperties() >> [lastUpdated]

        when:
        List<DomainProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    void "test getEditableProperties constraints display false"() {
        given:
        PersistentProperty bar = Mock()
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getName() >> "bar"
            1 * getConstraints() >> Mock(ConstrainedProperty) { 1 * isDisplay() >> false }
        }
        domainModelService.domainPropertyFactory = Mock(DomainPropertyFactoryImpl) {
            1 * build(bar) >> domainProperty
        }
        1 * domainClass.getPersistentProperties() >> [bar]

        when:
        List<DomainProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    void "test getEditableProperties scaffold exclude"() {
        given:
        PersistentProperty foo = Mock()
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getName() >> "foo"
        }
        domainModelService.domainPropertyFactory = Mock(DomainPropertyFactoryImpl) {
            1 * build(foo) >> domainProperty
        }
        1 * domainClass.getPersistentProperties() >> [foo]

        when:
        List<DomainProperty> properties = domainModelService.getEditableProperties(domainClass).toList()

        then: "properties that are excluded in the scaffolded property aren't included"
        properties.empty
    }

    private PersistentEntity mockDomainClass(MappingContext mappingContext, Class clazz) {
        PersistentEntity persistentEntity = mappingContext.addPersistentEntity(clazz)
        GrailsDomainClass grailsDomainClass = new DefaultGrailsDomainClass(clazz, [:])
        mappingContext.addEntityValidator(persistentEntity, new GrailsDomainClassValidator(domainClass: grailsDomainClass))
        persistentEntity
    }

    @Unroll
    void "test hasPropertyType - expect property #propertyType to exist: #expected"() {
        given:
        MappingContext mappingContext = new KeyValueMappingContext("test")
        PersistentEntity persistentEntity = mockDomainClass(mappingContext, ScaffoldedDomain)
        mockDomainClass(mappingContext, EmbeddedAssociate)
        DomainPropertyFactory domainPropertyFactory = new DomainPropertyFactoryImpl()
        domainPropertyFactory.trimStrings = true
        domainPropertyFactory.convertEmptyStringsToNull = true
        domainPropertyFactory.grailsDomainClassMappingContext = mappingContext
        domainModelService.domainPropertyFactory = domainPropertyFactory
        when:
        Boolean hasType = domainModelService.hasPropertyType(persistentEntity, propertyType)

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
        Long id
        Long version
        TimeZone timeZone
        Calendar cal
    }
}
