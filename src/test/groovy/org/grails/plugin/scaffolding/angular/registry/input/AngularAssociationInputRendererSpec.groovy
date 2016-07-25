package org.grails.plugin.scaffolding.angular.registry.input

import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Basic
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.datastore.mapping.model.types.Simple
import org.grails.plugin.scaffolding.ClosureCapture
import org.grails.plugin.scaffolding.ClosureCaptureSpecification
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.DomainInputRenderer
import spock.lang.Shared
import spock.lang.Subject
import spock.lang.Unroll

@Subject(AngularAssociationInputRenderer)
class AngularAssociationInputRendererSpec extends ClosureCaptureSpecification {

    @Shared
    DomainInputRenderer renderer

    void setup() {
        renderer = new AngularAssociationInputRenderer("vm")
    }

    @Unroll
    void "test supports #type"() {
        given:
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getPersistentProperty() >> Mock(type)
        }

        when:
        Boolean supports = renderer.supports(domainProperty)

        then:
        supports == expected

        where:
        type      | expected
        OneToMany | true
        OneToOne  | true
        ManyToOne | true
        Basic     | false
        Simple    | false
    }

    void "test render ToMany bidirectional"() {
        given:
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getAssociatedType() >> Number
            1 * getAssociatedEntity() >> Mock(PersistentEntity) {
                1 * getIdentity() >> Mock(PersistentProperty) {
                    1 * getName() >> "bar"
                }
            }
            1 * getPersistentProperty() >> Mock(OneToMany) {
                1 * isBidirectional() >> true
            }
        }

        when:
        ClosureCapture closureCapture = getClosureCapture(renderer.renderInput([:], domainProperty))

        then:
        closureCapture.calls[0].name == "select"
        closureCapture.calls[0].args[0] == ""
        closureCapture.calls[0].args[1] == ["ng-options": "number for number in vm.numberList track by number.bar"]
    }

    void "test render ToMany not bidirectional"() {
        given:
        DomainProperty domainProperty = Mock(DomainProperty) {
            1 * getAssociatedType() >> Number
            1 * getAssociatedEntity() >> Mock(PersistentEntity) {
                1 * getIdentity() >> Mock(PersistentProperty) {
                    1 * getName() >> "bar"
                }
            }
            1 * getPersistentProperty() >> Mock(OneToMany) {
                1 * isBidirectional() >> false
            }
        }

        when:
        ClosureCapture closureCapture = getClosureCapture(renderer.renderInput([:], domainProperty))

        then:
        closureCapture.calls[0].name == "select"
        closureCapture.calls[0].args[0] == ""
        closureCapture.calls[0].args[1] == ["ng-options": "number for number in vm.numberList track by number.bar", "multiple": ""]
    }
}
