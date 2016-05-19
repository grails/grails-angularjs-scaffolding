package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor

/**
 * Created by Jim on 5/14/2016.
 */
interface AngularMarkupBuilder {

    String getControllerName()

    Map getStandardAttributes(BeanPropertyAccessor property)

    Closure renderPropertyDisplay(BeanPropertyAccessor property, Boolean includeControllerName)

    Closure renderAssociationDisplay(BeanPropertyAccessor property, Boolean includeControllerName)

    Closure renderProperty(BeanPropertyAccessor property)

    Closure renderString(BeanPropertyAccessor property)

    Closure renderInput(BeanPropertyAccessor property)

    Closure renderBoolean(BeanPropertyAccessor property)

    Closure renderNumber(BeanPropertyAccessor property)

    Closure renderURL(BeanPropertyAccessor property)

    Closure renderSelect(BeanPropertyAccessor property)

    Closure renderTextArea(BeanPropertyAccessor property)

    Closure renderDate(BeanPropertyAccessor property)

    Closure renderTime(BeanPropertyAccessor property)

}