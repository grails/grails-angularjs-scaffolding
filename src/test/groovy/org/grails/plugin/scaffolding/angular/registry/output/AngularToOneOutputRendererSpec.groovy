package org.grails.plugin.scaffolding.angular.registry.output

import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.DomainOutputRenderer
import spock.lang.Shared
import spock.lang.Subject

@Subject(AngularToOneOutputRenderer)
class AngularToOneOutputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    DomainOutputRenderer renderer

    void setup() {
        renderer = new AngularToOneOutputRenderer("vm")
    }

    void "test supports"() {
        given:
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getPersistentProperty() >> Mock(type)
        }

        when:
        Boolean supports = renderer.supports(domainProperty)

        then:
        supports == expected

        where:
        type      | expected
        OneToMany | false
        OneToOne  | true
        ManyToOne | true
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
        closureCapture.calls[0].args[0] == "{{number.foo.toString()}}"
        closureCapture.calls[0].args[1] == ["ui-sref": "number.show({id: number.foo.id})"]

        when:
        closureCapture = getClosureCapture(renderer.renderOutput(domainProperty))

        then:
        closureCapture.calls[0].name == "a"
        closureCapture.calls[0].args[0] == "{{vm.number.foo.toString()}}"
        closureCapture.calls[0].args[1] == ["ui-sref": "number.show({id: vm.number.foo.id})"]
    }
}
