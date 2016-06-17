package org.grails.plugin.scaffolding.angular.registry.input

import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.DomainInputRenderer
import spock.lang.Shared
import spock.lang.Subject

@Subject(AngularCurrencyInputRenderer)
class AngularCurrencyInputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    DomainInputRenderer renderer

    void setup() {
        renderer = new AngularCurrencyInputRenderer()
    }

    void "test render"() {
        given:
        ClosureCapture closureCapture
        String currencyCode = Currency.getInstance(Locale.default).currencyCode

        when:
        closureCapture = getClosureCapture(renderer.renderInput(["ng-model": "foo"], Mock(DomainProperty)))

        then:
        closureCapture.calls[0].name == "select"
        closureCapture.calls[0].args[0] == ["ng-init": "foo = '${currencyCode}'", "ng-model": "foo"]
    }

}
