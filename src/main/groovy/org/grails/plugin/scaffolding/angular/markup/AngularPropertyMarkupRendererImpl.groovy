package org.grails.plugin.scaffolding.angular.markup

import grails.util.GrailsNameUtils
import org.grails.scaffolding.markup.PropertyMarkupRendererImpl
import org.grails.scaffolding.model.property.DomainProperty
import org.springframework.beans.factory.annotation.Value

class AngularPropertyMarkupRendererImpl extends PropertyMarkupRendererImpl implements AngularPropertyMarkupRenderer {

    @Value('${grails.codegen.angular.controllerName:vm}')
    String controllerName

    @Override
    Map getStandardAttributes(DomainProperty property) {
        final String objectName = GrailsNameUtils.getPropertyName(property.rootBeanType)
        Map attributes = super.getStandardAttributes(property)
        attributes["ng-model"] = "$controllerName.$objectName.${attributes.name}"
        attributes
    }
}
