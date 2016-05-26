package grails.plugin.scaffolding.registry

import grails.plugin.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/24/2016.
 */
interface DomainOutputRenderer extends DomainRenderer {

    Closure renderListOutput(DomainProperty property)

    Closure renderOutput(DomainProperty property)
}
