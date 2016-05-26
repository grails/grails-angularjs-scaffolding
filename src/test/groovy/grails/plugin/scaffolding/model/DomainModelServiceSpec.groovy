package grails.plugin.scaffolding.model

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.model.property.DomainPropertyFactory
import grails.plugin.scaffolding.model.property.DomainPropertyFactoryImpl
import grails.validation.ConstrainedProperty
import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import spock.lang.Shared
import spock.lang.Specification

class DomainModelServiceSpec extends Specification implements MocksDomain {

    @Shared
    DomainModelServiceImpl domainModelService

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

    void "test hasProperty"() {
        given:
        MappingContext mappingContext = new KeyValueMappingContext("test")
        PersistentEntity persistentEntity = mockDomainClass(mappingContext, ScaffoldedDomain)
        mockDomainClass(mappingContext, EmbeddedAssociate)
        DomainPropertyFactory domainPropertyFactory = mockDomainPropertyFactory(mappingContext)
        domainModelService.domainPropertyFactory = domainPropertyFactory

        expect:
        domainModelService.hasProperty(persistentEntity) { DomainProperty p ->
            p.name == "timeZone"
        }
        domainModelService.hasProperty(persistentEntity) { DomainProperty p ->
            p.name == "locale"
        }
        !domainModelService.hasProperty(persistentEntity) { DomainProperty p ->
            p.name == "not here"
        }
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
