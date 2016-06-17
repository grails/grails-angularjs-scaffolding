package org.grails.plugin.scaffolding.angular.registry.input

import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.DomainInputRenderer
import spock.lang.Shared
import spock.lang.Subject

@Subject(AngularTimeZoneInputRenderer)
class AngularTimeZoneInputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    DomainInputRenderer renderer

    void setup() {
        renderer = new AngularTimeZoneInputRenderer()
    }

    void "test render"() {
        given:
        ClosureCapture closureCapture
        String timeZoneId = TimeZone.default.ID

        when:
        closureCapture = getClosureCapture(renderer.renderInput(["ng-model": "foo"], Mock(DomainProperty)))

        then:
        closureCapture.calls[0].name == "select"
        closureCapture.calls[0].args[0] == ["ng-init": "foo = '${timeZoneId}'", "ng-model": "foo"]
    }

}
