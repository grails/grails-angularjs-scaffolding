package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.ClosureCapture
import grails.plugin.scaffolding.ClosureCaptureSpecification
import grails.plugin.scaffolding.model.property.DomainProperty
import grails.validation.ConstrainedProperty
import spock.lang.Shared
import spock.lang.Subject

@Subject(TextareaInputRenderer)
class TextareaInputRendererSpec extends ClosureCaptureSpecification {


    @Shared
    TextareaInputRenderer renderer

    void setup() {
        renderer = new TextareaInputRenderer()
    }

    void "test supports"() {
        given:
        DomainProperty prop = Mock(DomainProperty) {
            1 * getConstraints() >> Mock(ConstrainedProperty) {
                1 * getWidget() >> "textarea"
            }
        }

        expect:
        renderer.supports(prop)
    }

    void "test render"() {
        given:
        DomainProperty property = Mock(DomainProperty) {
            1 * getConstraints() >> Mock(ConstrainedProperty) {
                1 * getMaxSize() >> 20
            }
        }

        when:
        ClosureCapture closureCapture = getClosureCapture(renderer.renderInput([:], property))

        then:
        closureCapture.calls[0].name == "textarea"
        closureCapture.calls[0].args[0] == ["maxlength": 20]
    }
}
