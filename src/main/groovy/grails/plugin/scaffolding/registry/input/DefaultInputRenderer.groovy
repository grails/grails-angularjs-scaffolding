package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

class DefaultInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        true
    }

    @Override
    Closure renderInput(Map attributes, DomainProperty property) {
        { -> }
    }

}
