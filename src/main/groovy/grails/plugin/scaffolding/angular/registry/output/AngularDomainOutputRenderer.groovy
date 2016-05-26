package grails.plugin.scaffolding.angular.registry.output

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainOutputRenderer
import grails.util.GrailsNameUtils

/**
 * Created by Jim on 5/25/2016.
 */
abstract class AngularDomainOutputRenderer implements DomainOutputRenderer {

    abstract String getControllerName()

    protected String buildPropertyPath(DomainProperty property, Boolean includeControllerName) {
        StringBuilder sb = new StringBuilder()
        if (includeControllerName && controllerName) {
            sb.append(controllerName).append('.')
        }
        sb.append(GrailsNameUtils.getPropertyName(property.rootBeanType)).append('.')
        sb.append(property.pathFromRoot)
        sb.toString()
    }

    protected String getPropertyName(DomainProperty property) {
        GrailsNameUtils.getPropertyName(property.rootBeanType)
    }

    @Override
    Closure renderListOutput(DomainProperty property) {
        renderOutput(getPropertyName(property), buildPropertyPath(property, false))
    }

    @Override
    Closure renderOutput(DomainProperty property) {
        renderOutput(getPropertyName(property), buildPropertyPath(property, true))
    }

    abstract protected Closure renderOutput(String propertyName, String propertyPath)

}
