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
import org.springframework.context.MessageSource

import javax.annotation.Resource
import java.sql.Blob

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
    AngularElementBuilderImpl angularElementBuilder

    @Value('${grails.plugin.angular.scaffolding.controllerName:vm}')
    String controllerName

    String lineSeparator = System.getProperty("line.separator")

    String outputMarkupContent(Closure closure) {
        FastStringWriter writer = new FastStringWriter()
        MarkupBuilder markupBuilder = new MarkupBuilder(writer)
        markupBuilder.doubleQuotes = true
        markupBuilder.escapeAttributes = false
        closure.delegate = markupBuilder
        closure.call()
        writer.toString()
    }

    String renderEditEmbedded(def bean, BeanPropertyAccessor property) {
        String legendText = resolveMessage(property.labelKeys, property.defaultLabel)
        outputMarkupContent {
            fieldset(class: "embedded ${formFieldsTemplateService.toPropertyNameFormat(property.propertyType)}") {
                legend(legendText)
                domainModelService.getEditableProperties(property.persistentProperty.component).each { GrailsDomainClassProperty embedded ->
                    renderEdit(beanPropertyAccessorFactory.accessorFor(bean, "${property.pathFromRoot}.${embedded.name}"), delegate)
                }
            }
        }
    }

    String renderEdit(BeanPropertyAccessor property) {
        outputMarkupContent {
            renderEdit(property, delegate)
        }
    }

    void renderEdit(BeanPropertyAccessor property, MarkupBuilder markupBuilder) {
        List classes = ['fieldcontain']
        if (property.required) {
            classes << 'required'
        }
        Closure renderProperty = angularElementBuilder.renderElement(property)
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

    String getDisplayWidget(BeanPropertyAccessor property, String controllerName) {
        "{{${controllerName ? controllerName + "." : ""}${GrailsNameUtils.getPropertyName(property.beanType)}.${property.pathFromRoot}}}"
    }


    String getDisplayWidget(BeanPropertyAccessor property) {
        getDisplayWidget(property, controllerName)
    }

    String renderDisplay(def bean, BeanPropertyAccessor property) {
        outputMarkupContent {
            li(class: 'fieldcontain') {
                span([id: "${property.pathFromRoot}-label", class: "property-label"], getLabelText(property))
                div([class: "property-value", "aria-labelledby": "${property.pathFromRoot}-label"]) {

                    def persistentProperty = property.persistentProperty
                    if (persistentProperty?.association) {
                        if (persistentProperty.embedded) {
                            domainModelService.getVisibleProperties(property.persistentProperty.component).each { GrailsDomainClassProperty embedded ->
                                mkp.yieldUnescaped(lineSeparator)
                                mkp.yieldUnescaped renderDisplay(bean, beanPropertyAccessorFactory.accessorFor(bean, "${property.pathFromRoot}.${embedded.name}"))
                            }
                        }
                        /* else if (persistentProperty.oneToMany || persistentProperty.manyToMany) {
                            return displayAssociationList(model.value, persistentProperty.referencedDomainClass)
                        } else {
                            return displayAssociation(model.value, persistentProperty.referencedDomainClass)
                        }*/
                    } else {
                        span(getDisplayWidget(property))
                    }
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
