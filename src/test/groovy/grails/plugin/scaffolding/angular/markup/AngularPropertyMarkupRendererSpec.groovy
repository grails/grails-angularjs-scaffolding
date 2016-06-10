package grails.plugin.scaffolding.angular.markup

import org.grails.scaffolding.model.property.DomainProperty
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Jim on 5/26/2016.
 */
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

