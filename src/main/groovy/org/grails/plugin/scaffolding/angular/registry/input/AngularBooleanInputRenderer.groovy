package org.grails.plugin.scaffolding.angular.registry.input

import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.input.BooleanInputRenderer

/**
 * Created by Jim on 8/25/2016.
 */
class AngularBooleanInputRenderer extends BooleanInputRenderer {

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty domainProperty) {
        defaultAttributes['ng-init'] = "${defaultAttributes["ng-model"]} = false"
        super.renderInput(defaultAttributes, domainProperty)
    }
}
