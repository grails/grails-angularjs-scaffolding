package grails.plugin.scaffolding.markup

import grails.plugin.scaffolding.model.property.DomainProperty
import groovy.xml.MarkupBuilder
import org.grails.datastore.mapping.model.PersistentEntity

/**
 * Created by Jim on 5/23/2016.
 */
interface ContextMarkupRenderer {

    Closure listOutputContext(PersistentEntity domainClass, List<DomainProperty> properties, Closure content)

    Closure inputContext(PersistentEntity domainClass, Closure content)

    Closure inputContext(DomainProperty property, Closure content)

    Closure outputContext(PersistentEntity domainClass, Closure content)

    Closure outputContext(DomainProperty property, Closure content)

    Closure embeddedOutputContext(DomainProperty property, Closure content)

    Closure embeddedInputContext(DomainProperty property, Closure content)

}