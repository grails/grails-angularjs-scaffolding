package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

class DefaultDomainInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        false
    }

    @Override
    Closure renderInput(Map attributes, DomainProperty property) {
        { -> }
    }

}
