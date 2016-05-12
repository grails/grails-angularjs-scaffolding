package grails.plugin.angular.scaffolding.renderers

import grails.plugin.angular.scaffolding.element.ElementType
import grails.plugin.formfields.BeanPropertyAccessor

interface AngularPropertyRenderer {

    String getControllerName()

    ElementType getElementType(BeanPropertyAccessor property)

    String renderEmbedded(def bean, BeanPropertyAccessor property)

    String renderEdit(BeanPropertyAccessor property)

    String getWidget(BeanPropertyAccessor property)

    String getDisplayWidget(BeanPropertyAccessor property)

    String renderDisplay(BeanPropertyAccessor property)

    CharSequence getLabelText(BeanPropertyAccessor property)

    CharSequence resolveMessage(List<String> keysInPreferenceOrder, String defaultMessage)
}