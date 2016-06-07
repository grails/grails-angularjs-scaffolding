package grails.plugin.scaffolding.registry

import grails.plugin.scaffolding.model.property.DomainProperty

/**
 * A class used to render markup for a domain class property
 *
 * @author James Kleeh
 */
interface DomainRenderer {

    /**
     * Determines if the renderer supports rendering the given property
     *
     * @param property The domain property to be rendered
     * @return Whether or not the property is supported
     */
    boolean supports(DomainProperty property)

}