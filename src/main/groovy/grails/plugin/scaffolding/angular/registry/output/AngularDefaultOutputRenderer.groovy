package grails.plugin.scaffolding.angular.registry.output

import grails.plugin.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/25/2016.
 */
class AngularDefaultOutputRenderer extends AngularDomainOutputRenderer {

    final String controllerName

    AngularDefaultOutputRenderer(String controllerName) {
        this.controllerName = controllerName
    }

    @Override
    boolean supports(DomainProperty property) {
        true
    }

    @Override
    protected Closure renderOutput(String propertyName, String propertyPath) {
        { ->
            span("{{${propertyPath}}}")
        }
    }


}
