package grails.plugin.scaffolding.angular.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.input.FileInputRenderer

/**
 * Created by Jim on 5/24/2016.
 */
class AngularFileInputRenderer extends FileInputRenderer {

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        defaultAttributes['file-model'] = defaultAttributes.remove('ng-model')
        super.renderInput(defaultAttributes, property)
    }
}
