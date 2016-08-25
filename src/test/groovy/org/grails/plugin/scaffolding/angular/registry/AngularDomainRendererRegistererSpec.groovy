package org.grails.plugin.scaffolding.angular.registry

import grails.validation.Constrained
import grails.validation.ConstrainedProperty
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRenderer
import org.grails.plugin.scaffolding.angular.registry.input.*
import org.grails.plugin.scaffolding.angular.registry.output.AngularDateOutputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularIdOutputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularToManyOutputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularToOneOutputRenderer
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.DomainInputRendererRegistry
import org.grails.scaffolding.registry.DomainOutputRendererRegistry
import org.grails.scaffolding.registry.DomainRendererRegisterer
import org.grails.scaffolding.registry.input.*
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.sql.Time

@Subject(AngularDomainRendererRegisterer)
class AngularDomainRendererRegistererSpec extends Specification {

    @Shared
    DomainInputRendererRegistry domainInputRendererRegistry

    @Shared
    DomainOutputRendererRegistry domainOutputRendererRegistry

    void setup() {
        domainInputRendererRegistry = new DomainInputRendererRegistry()
        domainOutputRendererRegistry = new DomainOutputRendererRegistry()
        new DomainRendererRegisterer(
            domainInputRendererRegistry: domainInputRendererRegistry,
            domainOutputRendererRegistry: domainOutputRendererRegistry
        ).registerRenderers()
        new AngularDomainRendererRegisterer(
            domainInputRendererRegistry: domainInputRendererRegistry,
            domainOutputRendererRegistry: domainOutputRendererRegistry,
            propertyMarkupRenderer: Stub(AngularPropertyMarkupRenderer) {
                getControllerName() >> "vm"
            }
        ).registerRenderers()
    }


    void "test the InList renderer is returned for String"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> String
            getConstraints() >> Stub(Constrained) {
                getInList() >> ["foo"]
            }
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof InListInputRenderer
    }

    void "test the Textarea renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> String
            getConstraints() >> Stub(ConstrainedProperty) {
                getWidget() >> "textarea"
            }
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof TextareaInputRenderer
    }

    void "test the String renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> String
            getConstraints() >> Stub(ConstrainedProperty)
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof StringInputRenderer
    }

    void "test the Boolean renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Boolean
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof AngularBooleanInputRenderer
    }

    void "test the InList renderer is returned for Number"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Long
            getConstraints() >> Stub(Constrained) {
                getInList() >> [1L, 2L]
            }
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof InListInputRenderer
    }

    void "test the Number renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Long
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof NumberInputRenderer
    }

    void "test the URL renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> URL
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof UrlInputRenderer
    }

    enum Fruit {APPLE,ORANGE,BANANA,PEAR};

    void "test the Enum renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Fruit
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof EnumInputRenderer
    }

    void "test the Date renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Calendar
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof DateInputRenderer
    }

    void "test the Time renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Time
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof TimeInputRenderer
    }


    void "test the File renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> byte[]
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof AngularFileInputRenderer
    }

    void "test the TimeZone renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> TimeZone
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof AngularTimeZoneInputRenderer
    }

    void "test the Currency renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Currency
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof AngularCurrencyInputRenderer
    }

    void "test the Locale renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Locale
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof LocaleInputRenderer
    }

    void "test the Default renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Specification
            getConstraints() >> Stub(ConstrainedProperty) {
                getWidget() >> ""
            }
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof DefaultInputRenderer
    }

    void "test the BiDirectionalToMany renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Set
            getPersistentProperty() >> Stub(OneToMany) {
                isBidirectional() >> true
            }
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof AngularBidirectionalToManyInputRenderer
    }

    void "test the Association renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Set
            getPersistentProperty() >> Stub(OneToMany) {
                isBidirectional() >> false
            }
        }

        expect:
        domainInputRendererRegistry.get(domainProperty) instanceof AngularAssociationInputRenderer
    }

    void "test the Date output renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getType() >> Calendar
        }

        expect:
        domainOutputRendererRegistry.get(domainProperty) instanceof AngularDateOutputRenderer
    }

    void "test the Id output renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getDomainClass() >> Mock(PersistentEntity) {
                1 * isIdentityName("id") >> true
            }
            getName() >> "id"
        }

        expect:
        domainOutputRendererRegistry.get(domainProperty) instanceof AngularIdOutputRenderer
    }

    void "test the toMany output renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getPersistentProperty() >> Stub(OneToMany)

        }

        expect:
        domainOutputRendererRegistry.get(domainProperty) instanceof AngularToManyOutputRenderer
    }

    void "test the toOne output renderer is returned"() {
        given:
        DomainProperty domainProperty = Stub(DomainProperty) {
            getPersistentProperty() >> Stub(OneToOne)

        }

        expect:
        domainOutputRendererRegistry.get(domainProperty) instanceof AngularToOneOutputRenderer
    }

}
