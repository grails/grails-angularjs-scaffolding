package grails.plugin.angular.scaffolding.renderers

import grails.plugin.angular.scaffolding.element.ElementType
import grails.plugin.formfields.BeanPropertyAccessor

interface AngularPropertyRenderer {

    String getControllerName()

    ElementType getElementType(BeanPropertyAccessor property)

    String renderEditEmbedded(def bean, BeanPropertyAccessor property)

    String renderEdit(BeanPropertyAccessor property)

    String renderDisplay(def bean, BeanPropertyAccessor property)

    String getWidget(BeanPropertyAccessor property)

    String getDisplayWidget(BeanPropertyAccessor property)

    String getDisplayWidget(BeanPropertyAccessor property, String controllerName)

    String getLabelText(BeanPropertyAccessor property)

    String resolveMessage(List<String> keysInPreferenceOrder, String defaultMessage)
}