package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.ClosureCapture
import grails.plugin.scaffolding.ClosureCaptureSpecification
import grails.plugin.scaffolding.model.property.DomainProperty
import spock.lang.Shared
import spock.lang.Subject

/**
 * Created by Jim on 6/6/2016.
 */
@Subject(TimeInputRenderer)
class TimeInputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    TimeInputRenderer renderer

    void setup() {
        renderer = new TimeInputRenderer()
    }

    void "test supports"() {
        given:
        DomainProperty prop = Mock(DomainProperty) {
            1 * getType() >> java.sql.Time
        }

        expect:
        renderer.supports(prop)
    }

    void "test render"() {
        when:
        ClosureCapture closureCapture = getClosureCapture(renderer.renderInput([:], Mock(DomainProperty)))

        then:
        closureCapture.calls[0].name == "input"
        closureCapture.calls[0].args[0] == ["type": "datetime-local"]
    }
}
