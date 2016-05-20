package grails.plugin.angular.scaffolding.renderers

import grails.plugin.angular.scaffolding.model.property.DomainProperty
import org.grails.datastore.mapping.model.PersistentEntity

interface AngularPropertyRenderer {

    String renderEditEmbedded(DomainProperty property)

    String renderEdit(DomainProperty property)

    String renderDisplay(PersistentEntity domainClass)

    String renderPropertyDisplay(DomainProperty property, Boolean includeControllerName)

    String getLabelText(DomainProperty property)

    String resolveMessage(List<String> keysInPreferenceOrder, String defaultMessage)
}