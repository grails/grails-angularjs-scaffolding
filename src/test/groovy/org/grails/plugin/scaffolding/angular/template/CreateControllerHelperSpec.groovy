package org.grails.plugin.scaffolding.angular.template

import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.Embedded
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.scaffolding.model.property.DomainProperty
import spock.lang.Specification
import spock.lang.Subject

/**
 * Created by Jim on 6/14/2016.
 */
@Subject(CreateControllerHelper)
class CreateControllerHelperSpec extends Specification {

    private DomainProperty build(String name, Class type, Class entityType) {
        Mock(DomainProperty) {
            1 * getPersistentProperty() >> Mock(type)
            if (type == ManyToOne) {
                1 * getAssociatedEntity() >> Mock(PersistentEntity) {
                    1 * getJavaClass() >> entityType
                }
                1 * getName() >> name
            }
        }
    }

    void "test constructor"() {
        given:
        CreateControllerHelper createControllerHelper = new CreateControllerHelper([
                build("a", Embedded, String),
                build("b", OneToOne, String),
                build("c", ManyToOne, String),
                build("d", ManyToMany, String),
                build("e", ManyToOne, Number),
        ])

        when:
        List statements = createControllerHelper.controllerStatements

        then:
        statements.size() == 2
        statements[0].className == "String"
        statements[0].propertyName == "c"
        statements[0].parameterName == "cId"
        statements[1].className == "Number"
        statements[1].propertyName == "e"
        statements[1].parameterName == "eId"

        when:
        String stateParams = createControllerHelper.stateParams

        then:
        stateParams == '{"cId":null,"eId":null}'

    }
}
