package grails.plugin.angular.scaffolding.renderers

import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.element.*
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.formfields.BeanPropertyAccessor
import grails.plugin.formfields.BeanPropertyAccessorFactory
import grails.plugin.formfields.FormFieldsTemplateService
import grails.util.GrailsNameUtils
import groovy.xml.MarkupBuilder
import org.grails.buffer.FastStringWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import javax.annotation.Resource
import java.sql.Blob

class AngularPropertyRendererImpl implements AngularPropertyRenderer {

    @Resource
    def messageSource

    @Autowired
    DomainModelService domainModelService

    @Autowired
    BeanPropertyAccessorFactory beanPropertyAccessorFactory

    @Value('${grails.plugin.angular.scaffolding.controllerName:"vm"}')
    String controllerName

    String lineSeparator = System.getProperty("line.separator")

    String renderEmbedded(def bean, BeanPropertyAccessor property) {

        def legendText = resolveMessage(property.labelKeys, property.defaultLabel)
        println property
        def writer = new FastStringWriter()
        MarkupBuilder markupBuilder = new MarkupBuilder(writer)
        markupBuilder.doubleQuotes = true
        markupBuilder.fieldset(class: "embedded ${FormFieldsTemplateService.toPropertyNameFormat(property.propertyType)}") {
            legend(legendText)
            domainModelService.getEditableProperties(property.persistentProperty.component).each { GrailsDomainClassProperty embedded ->
                mkp.yieldUnescaped(lineSeparator)
                mkp.yieldUnescaped renderEdit(beanPropertyAccessorFactory.accessorFor(bean, "${property.pathFromRoot}.${embedded.name}"))
            }

        }
        writer.toString()
    }

    String renderEdit(BeanPropertyAccessor property) {
        def classes = ['fieldcontain']
        if (property.required) classes << 'required'

        def writer = new FastStringWriter()
        MarkupBuilder markupBuilder = new MarkupBuilder(writer)
        markupBuilder.doubleQuotes = true
        markupBuilder.div(class: classes.join(' ')) {
            label(for: property.pathFromRoot, getLabelText(property)) {
                if (property.required) {
                    span(class: 'required-indicator', '*')
                }
            }
            // TODO: encoding information of widget gets lost - don't use MarkupBuilder
            def widget = getWidget(property)
            if(widget != null) {
                mkp.yieldUnescaped(lineSeparator)
                mkp.yieldUnescaped widget
            }

        }
        writer.toString()

    }

    ElementType getElementType(BeanPropertyAccessor property) {
        if (property.propertyType in [String, null]) {
            ElementType.STRING
        } else if (property.propertyType in [boolean, Boolean]) {
            ElementType.BOOLEAN
        } else if (property.propertyType.isPrimitive() || property.propertyType in Number) {
            ElementType.NUMBER
        } else if (property.propertyType in URL) {
            ElementType.URL
        } else if (property.propertyType.isEnum()) {
            ElementType.ENUM
        } else if (property.persistentProperty?.oneToOne || property.persistentProperty?.manyToOne || property.persistentProperty?.manyToMany) {
            ElementType.ASSOCIATION
        } else if (property.persistentProperty?.oneToMany) {
            ElementType.ONETOMANY
        } else if (property.propertyType in [Date, Calendar, java.sql.Date]) {
            ElementType.DATE
        } else if (property.propertyType in java.sql.Time) {
            ElementType.TIME
        } else if (property.propertyType in [byte[], Byte[], Blob]) {
            ElementType.FILE
        } else if (property.propertyType in [TimeZone, Currency, Locale]) {
            ElementType.SPECIAL
        }
    }

    String getWidget(BeanPropertyAccessor property) {
        Element element

        ElementType elementType = getElementType(property)

        switch(elementType) {
            case ElementType.STRING:
                element = new StringInput(property)
                break
            case ElementType.BOOLEAN:
                element = new BooleanInput(property)
                break
            case ElementType.NUMBER:
                element = new NumberInput(property)
                break
            case ElementType.URL:
                element = new Input(property, "url")
                break
            case ElementType.ENUM:
                element = new Select(property)
                break
            case ElementType.ASSOCIATION:
                // TODO case association
            case ElementType.ONETOMANY:
                // TODO case oneToMany
                break
            case ElementType.DATE:
                element = new Input(property, "date")
                break
            case ElementType.TIME:
                element = new Input(property, "datetime-local")
                break
            case ElementType.FILE:
                // TODO case file
            case ElementType.SPECIAL:
                // TODO case timezone,currency,locale
                break
        }

        element?.render() ?: ""
    }

    String getDisplayWidget(BeanPropertyAccessor property) {
        "{{${controllerName ? controllerName + "." : ""}${GrailsNameUtils.getPropertyName(property.beanType)}.${property.pathFromRoot}}}"
    }

    String renderDisplay(BeanPropertyAccessor property) {
        def writer = new FastStringWriter()
        MarkupBuilder markupBuilder = new MarkupBuilder(writer)
        markupBuilder.doubleQuotes = true
        markupBuilder.li(class: 'fieldcontain') {
            span([id: "${property.pathFromRoot}-label", class: "property-label"], getLabelText(property))
            div([class: "property-value", "aria-labelledby": "${property.pathFromRoot}-label"]) {

                def persistentProperty = property.persistentProperty
                if (persistentProperty?.association) {
      /*              if (persistentProperty.embedded) {
                        return (attrs.displayStyle == 'table') ? model.value?.toString().encodeAsHTML() :
                                displayEmbedded(model.value, persistentProperty.component, attrs, templatesFolder)
                    } else if (persistentProperty.oneToMany || persistentProperty.manyToMany) {
                        return displayAssociationList(model.value, persistentProperty.referencedDomainClass)
                    } else {
                        return displayAssociation(model.value, persistentProperty.referencedDomainClass)
                    }*/
                    null
                }

                span(getDisplayWidget(property))

            }
        }

        writer.toString()
    }

    CharSequence getLabelText(BeanPropertyAccessor property) {
        def labelText
        if (property.labelKeys) {
            labelText = resolveMessage(property.labelKeys, property.defaultLabel)
        }
        if (!labelText) {
            labelText = property.defaultLabel
        }
        labelText
    }

    CharSequence resolveMessage(List<String> keysInPreferenceOrder, String defaultMessage) {
        def message = keysInPreferenceOrder.findResult { key ->
            messageSource.getMessage(key, [].toArray(), defaultMessage, Locale.default) ?: null
        }
        message ?: defaultMessage
    }
}
