package grails.plugin.angular.scaffolding.renderers

import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.element.*
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.formfields.BeanPropertyAccessor
import grails.plugin.formfields.BeanPropertyAccessorFactory
import grails.plugin.formfields.FormFieldsTemplateService
import groovy.xml.MarkupBuilder
import org.grails.buffer.FastStringWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource

import javax.annotation.Resource

class AngularPropertyRendererImpl implements AngularPropertyRenderer {

    @Resource
    MessageSource messageSource

    @Autowired
    DomainModelService domainModelService

    @Autowired
    BeanPropertyAccessorFactory beanPropertyAccessorFactory

    @Autowired
    FormFieldsTemplateService formFieldsTemplateService

    @Autowired
    AngularMarkupBuilderImpl angularMarkupBuilder


    static String outputMarkupContent(Closure closure) {
        FastStringWriter writer = new FastStringWriter()
        MarkupBuilder markupBuilder = new MarkupBuilder(writer)
        markupBuilder.doubleQuotes = true
        markupBuilder.escapeAttributes = false
        closure.delegate = markupBuilder
        if (closure.maximumNumberOfParameters == 1) {
            closure.call(markupBuilder)
        } else {
            closure.call()
        }
        writer.toString()
    }

    String renderEditEmbedded(def bean, BeanPropertyAccessor property) {
        String legendText = resolveMessage(property.labelKeys, property.defaultLabel)
        outputMarkupContent { MarkupBuilder markupBuilder ->
            fieldset(class: "embedded ${formFieldsTemplateService.toPropertyNameFormat(property.propertyType)}") {
                legend(legendText)
                domainModelService.getEditableProperties(property.persistentProperty.component).each { GrailsDomainClassProperty embedded ->
                    renderEdit(beanPropertyAccessorFactory.accessorFor(bean, "${property.pathFromRoot}.${embedded.name}"), markupBuilder)
                }
            }
        }
    }

    String renderEdit(BeanPropertyAccessor property) {
        outputMarkupContent { MarkupBuilder markupBuilder ->
            renderEdit(property, markupBuilder)
        }
    }

    protected void renderEdit(BeanPropertyAccessor property, MarkupBuilder markupBuilder) {
        List classes = ['fieldcontain']
        if (property.required) {
            classes << 'required'
        }
        Closure renderProperty = angularMarkupBuilder.renderProperty(property)
        renderProperty.delegate = markupBuilder
        markupBuilder.div(class: classes.join(' ')) {
            label(for: property.pathFromRoot, getLabelText(property)) {
                if (property.required) {
                    span(class: 'required-indicator', '*')
                }
            }
            renderProperty.call()
        }
    }

    String renderDisplay(def bean, GrailsDomainClass domainClass) {
        outputMarkupContent { MarkupBuilder markupBuilder ->
            renderDisplay(bean, domainClass, markupBuilder)
        }
    }

    protected void renderDisplay(GrailsDomainClass domainClass, MarkupBuilder markupBuilder, Closure closure) {
        markupBuilder.ol(class: "property-list ${domainClass.propertyName}") {
            domainModelService.getVisibleProperties(domainClass).each(closure)
        }
    }

    protected void renderDisplay(def bean, GrailsDomainClass domainClass, MarkupBuilder markupBuilder) {
        renderDisplay(domainClass, markupBuilder) { GrailsDomainClassProperty property ->
            renderDisplayField(bean, beanPropertyAccessorFactory.accessorFor(bean, property.name), markupBuilder)
        }
    }

    protected void renderDisplay(def bean, GrailsDomainClass domainClass, MarkupBuilder markupBuilder, BeanPropertyAccessor parentProperty) {
        renderDisplay(domainClass, markupBuilder) { GrailsDomainClassProperty property ->
            renderDisplayField(bean, beanPropertyAccessorFactory.accessorFor(bean, "${parentProperty.pathFromRoot}.${property.name}"), markupBuilder)
        }
    }

    protected void renderDisplayField(def bean, BeanPropertyAccessor property, MarkupBuilder markupBuilder) {
        markupBuilder.li(class: 'fieldcontain') {
            span([id: "${property.pathFromRoot}-label", class: "property-label"], getLabelText(property))
            div([class: "property-value", "aria-labelledby": "${property.pathFromRoot}-label"]) {
                def persistentProperty = property.persistentProperty
                if (persistentProperty?.association) {
                    if (persistentProperty.embedded) {
                        renderDisplay(bean, persistentProperty.component, markupBuilder, property)
                    }
                    /* else if (persistentProperty.oneToMany || persistentProperty.manyToMany) {
                        return displayAssociationList(model.value, persistentProperty.referencedDomainClass)
                    } else {
                        return displayAssociation(model.value, persistentProperty.referencedDomainClass)
                    }*/
                } else {
                    span(angularMarkupBuilder.renderPropertyDisplay(property, true))
                }
            }
        }
    }

    String getLabelText(BeanPropertyAccessor property) {
        def labelText
        if (property.labelKeys) {
            labelText = resolveMessage(property.labelKeys, property.defaultLabel)
        }
        if (!labelText) {
            labelText = property.defaultLabel
        }
        labelText
    }

    String resolveMessage(List<String> keysInPreferenceOrder, String defaultMessage) {
        def message = keysInPreferenceOrder.findResult { key ->
            messageSource.getMessage(key, [].toArray(), defaultMessage, Locale.default) ?: null
        }
        message ?: defaultMessage
    }
}
