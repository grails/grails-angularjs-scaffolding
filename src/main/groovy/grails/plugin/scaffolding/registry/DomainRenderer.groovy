package grails.plugin.scaffolding.registry

import grails.plugin.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/26/2016.
 */
interface DomainRenderer {

    boolean supports(DomainProperty property)

}