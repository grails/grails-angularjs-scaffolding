package grails.plugin.scaffolding.angular.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.input.TimeZoneInputRenderer

/**
 * Created by Jim on 5/24/2016.
 */
class AngularTimeZoneInputRenderer extends TimeZoneInputRenderer {

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        defaultAttributes['ng-init'] = "${defaultAttributes["ng-model"]} = '${getOptionKey(defaultOption)}'"
        super.renderInput(defaultAttributes, property)
    }
}
