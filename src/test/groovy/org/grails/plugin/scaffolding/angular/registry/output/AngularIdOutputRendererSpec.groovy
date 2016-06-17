package org.grails.plugin.scaffolding.angular.registry.output

import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.plugin.scaffolding.MocksDomain
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.model.property.DomainPropertyFactory
import org.grails.scaffolding.registry.DomainOutputRenderer
import spock.lang.Shared
import spock.lang.Subject

@Subject(AngularIdOutputRenderer)
class AngularIdOutputRendererSpec extends ClosureCaptureSpecification implements MocksDomain {

    @Shared
    DomainOutputRenderer renderer

    @Shared
    DomainPropertyFactory factory

    @Shared
    PersistentEntity fooDomain

    void setup() {
        renderer = new AngularIdOutputRenderer("vm")
        MappingContext mappingContext = new KeyValueMappingContext("test")
        fooDomain = mockDomainClass(mappingContext, Foo)
        factory = mockDomainPropertyFactory(mappingContext)
    }

    void "test supports"() {
        given:
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getDomainClass() >> Mock(PersistentEntity) {
                1 * isIdentityName("id") >> true
            }
            1 * getName() >> "id"
        }

        expect:
        renderer.supports(domainProperty)
    }

    void "test supports (real property)"() {
        given:

        DomainProperty domainProperty = factory.build(fooDomain.identity)

        expect:
        renderer.supports(domainProperty)
    }

    void "test output rendering"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getAssociatedType() >> Number
            getRootBeanType() >> Number
            getPathFromRoot() >> "foo"
        }
        ClosureCapture closureCapture

        when:
        closureCapture = getClosureCapture(renderer.renderListOutput(domainProperty))

        then:
        closureCapture.calls[0].name == "a"
        closureCapture.calls[0].args[0] == "{{number.foo}}"
        closureCapture.calls[0].args[1] == ["ui-sref": "number.show({id: number.foo})"]

        when:
        closureCapture = getClosureCapture(renderer.renderOutput(domainProperty))

        then:
        closureCapture.calls[0].name == "span"
        closureCapture.calls[0].args[0] == "{{vm.number.foo}}"
    }

    void "test output rendering (real property)"() {
        given:
        DomainProperty domainProperty = factory.build(fooDomain.identity)
        ClosureCapture closureCapture

        when:
        closureCapture = getClosureCapture(renderer.renderListOutput(domainProperty))

        then:
        closureCapture.calls[0].name == "a"
        closureCapture.calls[0].args[0] == "{{foo.bar}}"
        closureCapture.calls[0].args[1] == ["ui-sref": "foo.show({id: foo.bar})"]

        when:
        closureCapture = getClosureCapture(renderer.renderOutput(domainProperty))

        then:
        closureCapture.calls[0].name == "span"
        closureCapture.calls[0].args[0] == "{{vm.foo.bar}}"
    }

}

class Foo {
    Long id
    Long version
    String bar

    static mapping = {
        id name: "bar"
    }
}