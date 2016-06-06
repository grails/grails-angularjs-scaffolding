package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.ClosureCapture
import grails.plugin.scaffolding.ClosureCaptureSpecification
import grails.plugin.scaffolding.model.property.DomainProperty
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Subject(UrlInputRenderer)
class UrlInputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    UrlInputRenderer renderer

    void setup() {
        renderer = new UrlInputRenderer()
    }

    void "test supports"() {
        given:
        DomainProperty prop = Mock(DomainProperty) {
            1 * getType() >> URL
        }

        expect:
        renderer.supports(prop)
    }

    void "test render"() {
        when:
        ClosureCapture closureCapture = getClosureCapture(renderer.renderInput([:], Mock(DomainProperty)))

        then:
        closureCapture.calls[0].name == "input"
        closureCapture.calls[0].args[0] == ["type": "url"]
    }
}
