package grails.plugin.scaffolding.registry.output

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainOutputRenderer
import grails.util.GrailsNameUtils

/**
 * Created by Jim on 5/24/2016.
 */
class DefaultDomainOutputRenderer implements DomainOutputRenderer {

    protected String buildPropertyPath(DomainProperty property) {
        StringBuilder sb = new StringBuilder()
        sb.append(GrailsNameUtils.getPropertyName(property.rootBeanType)).append('.')
        sb.append(property.pathFromRoot)
        sb.toString()
    }

    @Override
    boolean supports(DomainProperty property) {
        return false
    }

    @Override
    Closure renderListOutput(DomainProperty property) {
        renderOutput(property)
    }

    @Override
    Closure renderOutput(DomainProperty property) {
        { ->
            "\${${buildPropertyPath(property)}}"
        }
    }
}
