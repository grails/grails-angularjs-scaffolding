package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer
import org.grails.datastore.mapping.model.types.Association

class AssociationInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        property.persistentProperty instanceof Association
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        { -> }
    }
}
