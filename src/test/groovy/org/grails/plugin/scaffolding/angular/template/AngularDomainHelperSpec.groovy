package org.grails.plugin.scaffolding.angular.template

import grails.web.mapping.UrlCreator
import grails.web.mapping.UrlMappings
import grails.web.mapping.exceptions.UrlMappingException
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.Embedded
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.scaffolding.model.property.DomainProperty
import spock.lang.Specification
import spock.lang.Subject

@Subject(AngularDomainHelper)
class AngularDomainHelperSpec extends Specification {

    private DomainProperty build(String name, Class type, Class entityType) {
        Mock(DomainProperty) {
            1 * getAssociatedType() >> type
            1 * getName() >> name
            1 * getPersistentProperty() >> Mock(entityType)
        }
    }

    void "test constructor"() {
        given:
        PersistentEntity domain = Mock(PersistentEntity) {
            2 * getDecapitalizedName() >> "foo"
        }
        List<DomainProperty> properties = [
                build("a", String, OneToMany),
                build("b", Number, OneToOne),
                build("c", Date, ManyToMany),
                build("d", TimeZone, ManyToOne),
                build("e", GString, Embedded),
        ]
        UrlMappings urlMappings = Mock(UrlMappings) {
            1 * getReverseMapping("foo", "index", null, null, "GET", [:]) >> Mock(UrlCreator) {
                1 * createRelativeURL("foo", "index", [:], 'UTF8') >> "/bar"
            }
        }

        when:
        AngularDomainHelper angularDomainHelper = new AngularDomainHelper(domain, properties, urlMappings)

        then:
        angularDomainHelper.uri == "bar"
        angularDomainHelper.getConfig == ', transformResponse: [angular.fromJson, domainToManyConversion("String", "a"), domainConversion("Number", "b"), domainToManyConversion("Date", "c"), domainConversion("TimeZone", "d"), domainConversion("GString", "e")]'
        angularDomainHelper.queryConfig == ', transformResponse: [angular.fromJson, domainListConversion("String", "a", "domainToManyConversion"), domainListConversion("Number", "b", "domainConversion"), domainListConversion("Date", "c", "domainToManyConversion"), domainListConversion("TimeZone", "d", "domainConversion"), domainListConversion("GString", "e", "domainConversion")]'
        angularDomainHelper.moduleConfig == ", domainListConversion, domainToManyConversion, domainConversion"
    }

    void "test constructor no urlMappings"() {
        given:
        PersistentEntity domain = Mock(PersistentEntity) {
            2 * getDecapitalizedName() >> "foo"
        }
        UrlMappings urlMappings = Mock(UrlMappings) {
            1 * getReverseMapping("foo", "index", null, null, "GET", [:]) >> {
                throw new UrlMappingException("test")
            }
        }

        when:
        AngularDomainHelper angularDomainHelper = new AngularDomainHelper(domain, [], urlMappings)

        then:
        angularDomainHelper.uri == "foo"
        angularDomainHelper.getConfig == ''
        angularDomainHelper.queryConfig == ''
        angularDomainHelper.moduleConfig == ""
    }
}
