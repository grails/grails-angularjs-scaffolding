package grails.plugin.scaffolding.markup

import grails.plugin.scaffolding.model.DomainModelService
import grails.plugin.scaffolding.model.property.DomainProperty
import org.grails.datastore.mapping.model.PersistentEntity
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Jim on 5/29/2016.
 */
class DomainMarkupRendererSpec extends Specification {

    @Shared
    DomainMarkupRendererImpl renderer

    void setup() {
        renderer = new DomainMarkupRendererImpl()
    }

    void "test renderListOutput"() {
        given:
        DomainProperty prop1 = Mock(DomainProperty) {
            1 * getName() >> "prop1"
        }
        DomainProperty prop2 = Mock(DomainProperty) {
            1 * getName() >> "prop2"
        }
        DomainProperty prop3 = Mock(DomainProperty) {
            1 * getName() >> "prop3"
        }
        List props = [prop1, prop2, prop3]
        renderer.domainModelService = Mock(DomainModelService) {
            1 * getShortListVisibleProperties(_ as PersistentEntity) >> props
        }
        renderer.contextMarkupRenderer = Mock(ContextMarkupRenderer) {
            1 * listOutputContext(_ as PersistentEntity, props, _ as Closure) >> { entity, properties, closure ->
                return { ->
                    properties.each { DomainProperty prop ->
                        div(closure.call(prop))
                    }
                }
            }
        }
        renderer.propertyMarkupRenderer = Mock(PropertyMarkupRenderer) {
            3 * renderListOutput(_ as DomainProperty) >> { DomainProperty prop ->
                println "returning closure"
                return { -> span(prop.name) }
            }
        }

        when:
        String output = renderer.renderListOutput(Mock(PersistentEntity))

        then:
        output == "<div>\n  <span>prop1</span>\n</div>\n<div>\n  <span>prop2</span>\n</div>\n<div>\n  <span>prop3</span>\n</div>"
    }
}
