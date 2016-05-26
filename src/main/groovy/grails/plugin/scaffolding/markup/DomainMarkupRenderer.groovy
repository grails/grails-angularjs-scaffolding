package grails.plugin.scaffolding.markup

import org.grails.datastore.mapping.model.PersistentEntity

interface DomainMarkupRenderer {

    String renderOutput(PersistentEntity domainClass)

    String renderListOutput(PersistentEntity domainClass)

    String renderForm(PersistentEntity domainClass)

}