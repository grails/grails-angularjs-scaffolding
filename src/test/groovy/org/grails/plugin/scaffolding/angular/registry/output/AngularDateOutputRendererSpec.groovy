package org.grails.plugin.scaffolding.angular.registry.output

import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.DomainOutputRenderer
import spock.lang.Shared
import spock.lang.Subject

@Subject(AngularDateOutputRenderer)
class AngularDateOutputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    DomainOutputRenderer renderer

    void setup() {
        renderer = new AngularDateOutputRenderer("vm")
    }

    void "test supports"() {
        given:
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getType() >> type
        }

        when:
        Boolean supports = renderer.supports(domainProperty)

        then:
        supports == expected

        where:
        type          | expected
        String        | false
        Date          | true
        java.sql.Date | true
        Calendar      | true
    }

    void "test render output"() {
        given:
        DomainProperty domainProperty = Mock(DomainProperty) {
            getAssociatedType() >> Number
            getRootBeanType() >> Number
            getPathFromRoot() >> "foo"
        }
        ClosureCapture closureCapture

        when:
        closureCapture = getClosureCapture(renderer.renderOutput(domainProperty))

        then:
        closureCapture.calls[0].name == "span"
        closureCapture.calls[0].args[0] == "{{vm.number.foo | date:'yyyy-MM-dd HH:mm:ss Z'}}"

        when:
        closureCapture = getClosureCapture(renderer.renderListOutput(domainProperty))

        then:
        closureCapture.calls[0].name == "span"
        closureCapture.calls[0].args[0] == "{{number.foo | date:'yyyy-MM-dd HH:mm:ss Z'}}"
    }
}
