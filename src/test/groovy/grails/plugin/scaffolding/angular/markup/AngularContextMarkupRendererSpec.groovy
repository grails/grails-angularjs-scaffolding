package grails.plugin.scaffolding.angular.markup

import grails.plugin.scaffolding.ClosureCapture
import grails.plugin.scaffolding.model.property.DomainProperty
import org.grails.datastore.mapping.model.PersistentEntity
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Jim on 5/26/2016.
 */
class AngularContextMarkupRendererSpec extends Specification {

    @Shared
    AngularContextMarkupRenderer renderer

    void setup() {
        renderer = new AngularContextMarkupRendererImpl(propertyMarkupRenderer: Stub(AngularPropertyMarkupRenderer) {
            getControllerName() >> "ctrl"
        })
    }

    void "test listOutputContext"() {
        given:
        PersistentEntity domain = Mock(PersistentEntity) {
            1 * getDecapitalizedName() >> "domain"
        }
        DomainProperty prop1 = Mock(DomainProperty) {
            1 * getDefaultLabel() >> "Prop 1"
            1 * getName() >> "prop1"
        }
        DomainProperty prop2 = Mock(DomainProperty) {
            1 * getDefaultLabel() >> "Prop 2"
            1 * getName() >> "prop2"
        }
        DomainProperty prop3 = Mock(DomainProperty) {
            1 * getDefaultLabel() >> "Prop 3"
            1 * getName() >> "prop3"
        }

        when:
        Closure closure = renderer.listOutputContext(domain, [prop1, prop2, prop3]) { DomainProperty prop ->
            prop.name
        }
        ClosureCapture closureCapture = new ClosureCapture()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = closureCapture
        closure.call()

        then:
        closureCapture.calls[0].name == "table"
        closureCapture.calls[0][0].name == "thead"
        closureCapture.calls[0][0][0].name == "tr"
        closureCapture.calls[0][0][0][0].name == "th"
        closureCapture.calls[0][0][0][0].args[0] == "Prop 1"
        closureCapture.calls[0][0][0][1].name == "th"
        closureCapture.calls[0][0][0][1].args[0] == "Prop 2"
        closureCapture.calls[0][0][0][2].name == "th"
        closureCapture.calls[0][0][0][2].args[0] == "Prop 3"
        closureCapture.calls[0][1].name == "tbody"
        closureCapture.calls[0][1][0].name == "tr"
        closureCapture.calls[0][1][0].args[0]["ng-class"] == "{'even': \$index%2 == 0, 'odd': \$index%2 == 1}"
        closureCapture.calls[0][1][0].args[0]["ng-repeat"] == "domain in ctrl.domainList"
        closureCapture.calls[0][1][0][0].name == "td"
        closureCapture.calls[0][1][0][0].args[0] == "prop1"
        closureCapture.calls[0][1][0][1].name == "td"
        closureCapture.calls[0][1][0][1].args[0] == "prop2"
        closureCapture.calls[0][1][0][2].name == "td"
        closureCapture.calls[0][1][0][2].args[0] == "prop3"
    }
}
