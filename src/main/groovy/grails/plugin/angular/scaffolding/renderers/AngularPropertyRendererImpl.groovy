package grails.plugin.angular.scaffolding.renderers

import grails.plugin.angular.scaffolding.element.*
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.model.property.DomainProperty
import grails.plugin.angular.scaffolding.model.property.DomainPropertyFactory
import grails.plugin.formfields.FormFieldsTemplateService
import groovy.xml.MarkupBuilder
import org.grails.buffer.FastStringWriter
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.Embedded
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.OneToMany
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource

import javax.annotation.Resource

class AngularPropertyRendererImpl implements AngularPropertyRenderer {

    @Resource
    MessageSource messageSource

    @Autowired
    DomainModelService domainModelService

    @Autowired
    FormFieldsTemplateService formFieldsTemplateService

    @Autowired
    AngularMarkupBuilderImpl angularMarkupBuilder

    @Autowired
    DomainPropertyFactory domainPropertyFactory


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

    String renderEditEmbedded(DomainProperty property) {
        String legendText = resolveMessage(property.labelKeys, property.defaultLabel)
        outputMarkupContent { MarkupBuilder markupBuilder ->
            fieldset(class: "embedded ${formFieldsTemplateService.toPropertyNameFormat(property.type)}") {
                legend(legendText)
                domainModelService.getEditableProperties(((Embedded)property.property).associatedEntity).each { DomainProperty embedded ->
                    embedded.rootProperty = property
                    renderEdit(embedded, markupBuilder)
                }
            }
        }
    }

    String renderEdit(DomainProperty property) {
        outputMarkupContent { MarkupBuilder markupBuilder ->
            renderEdit(property, markupBuilder)
        }
    }

    protected void renderEdit(DomainProperty property, MarkupBuilder markupBuilder) {
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

    String renderDisplay(PersistentEntity domainClass) {
        outputMarkupContent { MarkupBuilder markupBuilder ->
            renderDisplay(domainClass, markupBuilder)
        }
    }

    protected void renderDisplay(PersistentEntity domainClass, MarkupBuilder markupBuilder, Closure closure) {
        markupBuilder.ol(class: "property-list ${domainClass.decapitalizedName}") {
            domainModelService.getVisibleProperties(domainClass).each(closure)
        }
    }

    protected void renderDisplay(PersistentEntity domainClass, MarkupBuilder markupBuilder) {
        renderDisplay(domainClass, markupBuilder) { DomainProperty property ->
            renderDisplayField(property, markupBuilder)
        }
    }

    protected void renderDisplay(PersistentEntity domainClass, MarkupBuilder markupBuilder, DomainProperty parentProperty) {
        renderDisplay(domainClass, markupBuilder) { DomainProperty property ->
            property.rootProperty = parentProperty
            renderDisplayField(property, markupBuilder)
        }
    }

    protected void renderDisplayField(DomainProperty property, MarkupBuilder markupBuilder) {
        markupBuilder.li(class: 'fieldcontain') {
            span([id: "${property.pathFromRoot}-label", class: "property-label"], getLabelText(property))
            div([class: "property-value", "aria-labelledby": "${property.pathFromRoot}-label"]) {
                PersistentProperty persistentProperty = property.property
                if (persistentProperty instanceof Embedded) {
                    renderDisplay(persistentProperty.associatedEntity, markupBuilder, property)
                } else {
                    renderPropertyDisplay(property, true, markupBuilder)
                }
            }
        }
    }

    String renderPropertyDisplay(DomainProperty property, Boolean includeControllerName) {
        outputMarkupContent { MarkupBuilder markupBuilder ->
            renderPropertyDisplay(property, includeControllerName, markupBuilder)
        }
    }

    protected void renderPropertyDisplay(DomainProperty property, Boolean includeControllerName, MarkupBuilder markupBuilder) {
        PersistentProperty persistentProperty = property.property
        Closure propertyDisplay
        if (persistentProperty instanceof Association) {
            if (persistentProperty instanceof OneToMany || persistentProperty instanceof ManyToMany) {
                null
                //return displayAssociationList(model.value, persistentProperty.referencedDomainClass)
            } else {
                propertyDisplay = angularMarkupBuilder.renderAssociationDisplay(property, includeControllerName)
            }
        } else {
            propertyDisplay = angularMarkupBuilder.renderPropertyDisplay(property, includeControllerName)
        }
        if (propertyDisplay) {
            propertyDisplay.delegate = markupBuilder
            propertyDisplay.call()
        }
    }

    String getLabelText(DomainProperty property) {
        String labelText
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
