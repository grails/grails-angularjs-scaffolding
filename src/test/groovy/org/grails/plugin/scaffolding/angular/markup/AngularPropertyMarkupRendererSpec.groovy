package org.grails.plugin.scaffolding.angular.markup

import org.grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRenderer
import org.grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRendererImpl
import org.grails.scaffolding.model.property.DomainProperty
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Subject(AngularPropertyMarkupRendererImpl)
class AngularPropertyMarkupRendererSpec extends Specification {

    @Shared
    AngularPropertyMarkupRenderer renderer

    void setup() {
        renderer = new AngularPropertyMarkupRendererImpl(controllerName: "ctrl")
    }

    void "test getStandardAttributes"() {
        given:
        DomainProperty property = Mock(DomainProperty) {
            1 * getPathFromRoot() >> "city"
            1 * getRootBeanType() >> Object
        }

        when:
        Map attributes = renderer.getStandardAttributes(property)

        then:
        attributes["ng-model"] == "ctrl.object.city"
    }

}

