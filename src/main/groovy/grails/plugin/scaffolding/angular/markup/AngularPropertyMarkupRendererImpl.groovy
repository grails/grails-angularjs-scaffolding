package grails.plugin.scaffolding.angular.markup

import grails.plugin.scaffolding.markup.PropertyMarkupRendererImpl
import grails.plugin.scaffolding.model.property.DomainProperty
import grails.util.GrailsNameUtils
import org.springframework.beans.factory.annotation.Value

class AngularPropertyMarkupRendererImpl extends PropertyMarkupRendererImpl implements AngularPropertyMarkupRenderer {

    @Value('${grails.plugin.angular.scaffolding.controllerName:vm}')
    String controllerName

    @Override
    Map getStandardAttributes(DomainProperty property) {
        final String objectName = GrailsNameUtils.getPropertyName(property.rootBeanType)
        Map attributes = super.getStandardAttributes(property)
        attributes["ng-model"] = "$controllerName.$objectName.${attributes.name}"
        attributes
    }
}
