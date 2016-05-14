package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor

/**
 * Created by Jim on 5/14/2016.
 */
interface AngularElementBuilder {

    String getControllerName()

    ElementType getElementType(BeanPropertyAccessor property)

    Map getStandardAttributes(BeanPropertyAccessor property)

    Closure renderElement(BeanPropertyAccessor property)

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