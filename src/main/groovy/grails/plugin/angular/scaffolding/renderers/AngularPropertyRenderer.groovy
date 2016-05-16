package grails.plugin.angular.scaffolding.renderers

import grails.plugin.angular.scaffolding.element.ElementType
import grails.plugin.formfields.BeanPropertyAccessor
import groovy.xml.MarkupBuilder

interface AngularPropertyRenderer {

    String getControllerName()

    String renderEditEmbedded(def bean, BeanPropertyAccessor property)

    String renderEdit(BeanPropertyAccessor property)

    void renderEdit(BeanPropertyAccessor property, MarkupBuilder markupBuilder)

    String renderDisplay(def bean, BeanPropertyAccessor property)

    String getDisplayWidget(BeanPropertyAccessor property)

    String getDisplayWidget(BeanPropertyAccessor property, String controllerName)

    String getLabelText(BeanPropertyAccessor property)

    String resolveMessage(List<String> keysInPreferenceOrder, String defaultMessage)
}