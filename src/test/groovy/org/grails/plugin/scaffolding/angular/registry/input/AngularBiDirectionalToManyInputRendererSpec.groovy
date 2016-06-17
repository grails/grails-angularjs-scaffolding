package org.grails.plugin.scaffolding.angular.registry.input

import grails.gorm.annotation.Entity
import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.ToMany
import org.grails.datastore.mapping.model.types.ToOne
import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.plugin.scaffolding.MocksDomain
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.model.property.DomainPropertyFactory
import org.grails.scaffolding.registry.DomainInputRenderer
import spock.lang.Shared
import spock.lang.Subject

@Subject(AngularBidirectionalToManyInputRenderer)
class AngularBiDirectionalToManyInputRendererSpec extends ClosureCaptureSpecification implements MocksDomain {

    @Shared
    DomainInputRenderer renderer

    @Shared
    DomainPropertyFactory factory

    @Shared
    PersistentEntity fooDomain

    void setup() {
        renderer = new AngularBidirectionalToManyInputRenderer("vm")
        MappingContext mappingContext = new KeyValueMappingContext("test")
        mockDomainClass(mappingContext, Bar)
        fooDomain = mockDomainClass(mappingContext, Foo)
        factory = mockDomainPropertyFactory(mappingContext)
    }

    void "test render"() {
        given:
        PersistentProperty bars = fooDomain.getPropertyByName("bars")
        DomainProperty domainProperty = factory.build(bars)

        when:
        ClosureCapture closureCapture = getClosureCapture(renderer.renderInput([:], domainProperty))

        then:
        closureCapture.calls[0].name == "a"
        closureCapture.calls[0].args[0] == "Add Bar"
        closureCapture.calls[0].args[1] == ["ui-sref": "bar.create({fooId: vm.foo.code})"]
    }
}

@Entity
class Foo {
    String code
    static hasMany = [bars: Bar]
    static mapping = {
        id name: "code"
    }
}

@Entity
class Bar {
    String name
    static belongsTo = [foo: Foo]
}