package org.grails.plugin.scaffolding.angular.registry.input

import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.DomainInputRenderer
import spock.lang.Shared
import spock.lang.Subject

@Subject(AngularFileInputRenderer)
class AngularFileInputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    DomainInputRenderer renderer

    void setup() {
        renderer = new AngularFileInputRenderer()
    }

    void "test render"() {
        given:
        ClosureCapture closureCapture

        when:
        closureCapture = getClosureCapture(renderer.renderInput(["ng-model": "foo"], Mock(DomainProperty)))

        then:
        closureCapture.calls[0].name == "input"
        closureCapture.calls[0].args[0] == ["file-model": "foo", "type": "file"]
    }
}
