package grails.plugin.scaffolding.registry

import grails.plugin.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/23/2016.
 */
interface DomainInputRenderer extends DomainRenderer {

    Closure renderInput(Map defaultAttributes, DomainProperty property)

}
