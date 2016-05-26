package grails.plugin.scaffolding.angular.markup

import grails.plugin.scaffolding.markup.ContextMarkupRendererImpl
import grails.plugin.scaffolding.model.property.DomainProperty
import org.grails.datastore.mapping.model.PersistentEntity
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by Jim on 5/26/2016.
 */
class AngularContextMarkupRendererImpl extends ContextMarkupRendererImpl implements AngularContextMarkupRenderer {

    @Autowired
    AngularPropertyMarkupRenderer propertyMarkupRenderer

    String getControllerName() {
        propertyMarkupRenderer.controllerName
    }

    @Override
    Closure listOutputContext(PersistentEntity domainClass, List<DomainProperty> properties, Closure content) {
        final String propertyName = domainClass.decapitalizedName
        StringBuilder sb = new StringBuilder()
        if (controllerName) {
            sb.append(controllerName).append('.')
        }
        sb.append(propertyName).append("List")
        return { ->
            table {
                thead {
                    tr {
                        properties.each {
                            th(getDefaultTableHeader(it))
                        }
                    }
                }
                tbody {
                    tr(["ng-class": "{'even': \$index%2 == 0, 'odd': \$index%2 == 1}", "ng-repeat": "${propertyName} in ${sb.toString()}"]) {
                        properties.each {
                            td(content.call(it))
                        }
                    }
                }
            }
        }
    }
}
