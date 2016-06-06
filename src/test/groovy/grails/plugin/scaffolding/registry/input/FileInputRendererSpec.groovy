package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.ClosureCapture
import grails.plugin.scaffolding.ClosureCaptureSpecification
import grails.plugin.scaffolding.model.property.DomainProperty
import spock.lang.Shared
import spock.lang.Subject

import java.sql.Blob

@Subject(FileInputRenderer)
class FileInputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    FileInputRenderer renderer

    void setup() {
        renderer = new FileInputRenderer()
    }

    void "test supports"() {
        given:
        DomainProperty property

        when:
        property = Mock(DomainProperty) {
            1 * getType() >> type
        }

        then:
        renderer.supports(property)

        where:
        type | _
        byte[] | _
        Byte[] | _
        Blob | _
    }

    void "test render"() {
        when:
        ClosureCapture closureCapture = getClosureCapture(renderer.renderInput([:], Mock(DomainProperty)))

        then:
        closureCapture.calls[0].name == "input"
        closureCapture.calls[0].args[0] == ["type": "file"]
    }

}
