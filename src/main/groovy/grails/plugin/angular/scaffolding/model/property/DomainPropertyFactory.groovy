package grails.plugin.angular.scaffolding.model.property

import org.grails.datastore.mapping.model.PersistentProperty

/**
 * Created by Jim on 5/20/2016.
 */
interface DomainPropertyFactory {

    DomainProperty build(PersistentProperty persistentProperty)

    DomainProperty build(PersistentProperty rootProperty, PersistentProperty persistentProperty)
}
