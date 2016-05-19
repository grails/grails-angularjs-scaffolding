package grails.plugin.angular.scaffolding.renderers

import grails.core.GrailsDomainClass
import grails.plugin.formfields.BeanPropertyAccessor
import groovy.xml.MarkupBuilder

interface AngularPropertyRenderer {

    String renderEditEmbedded(def bean, BeanPropertyAccessor property)

    String renderEdit(BeanPropertyAccessor property)

    String renderDisplay(def bean, GrailsDomainClass domainClass)

    String renderPropertyDisplay(BeanPropertyAccessor property, Boolean includeControllerName)

    String getLabelText(BeanPropertyAccessor property)

    String resolveMessage(List<String> keysInPreferenceOrder, String defaultMessage)
}