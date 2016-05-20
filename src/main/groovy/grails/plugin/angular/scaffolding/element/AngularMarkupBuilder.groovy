package grails.plugin.angular.scaffolding.element

import grails.plugin.angular.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/14/2016.
 */
interface AngularMarkupBuilder {

    String getControllerName()

    Map getStandardAttributes(DomainProperty property)

    Closure renderPropertyDisplay(DomainProperty property, Boolean includeControllerName)

    Closure renderAssociationDisplay(DomainProperty property, Boolean includeControllerName)

    Closure renderProperty(DomainProperty property)

    Closure renderString(DomainProperty property)

    Closure renderInput(DomainProperty property)

    Closure renderBoolean(DomainProperty property)

    Closure renderNumber(DomainProperty property)

    Closure renderURL(DomainProperty property)

    Closure renderSelect(DomainProperty property)

    Closure renderTextArea(DomainProperty property)

    Closure renderDate(DomainProperty property)

    Closure renderTime(DomainProperty property)

}