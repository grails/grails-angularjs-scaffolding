package org.grails.plugin.scaffolding.angular.registry.input

import org.grails.scaffolding.model.property.DomainProperty
import org.grails.scaffolding.registry.input.CurrencyInputRenderer

/**
 * Created by Jim on 5/24/2016.
 */
class AngularCurrencyInputRenderer extends CurrencyInputRenderer {

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        defaultAttributes['ng-init'] = "${defaultAttributes["ng-model"]} = '${getOptionKey(defaultOption)}'"
        super.renderInput(defaultAttributes, property)
    }
}
