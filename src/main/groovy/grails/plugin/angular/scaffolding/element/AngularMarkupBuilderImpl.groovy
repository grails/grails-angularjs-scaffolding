package grails.plugin.angular.scaffolding.element

import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.model.property.DomainProperty
import grails.plugin.angular.scaffolding.model.property.PropertyType
import grails.util.GrailsNameUtils
import grails.validation.Constrained
import groovy.json.JsonOutput
import org.grails.datastore.mapping.model.types.ManyToMany
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

class AngularMarkupBuilderImpl implements AngularMarkupBuilder {

    @Autowired
    DomainModelService domainModelService

    @Value('${grails.plugin.angular.scaffolding.controllerName:vm}')
    String controllerName

    Map getStandardAttributes(DomainProperty property) {
        final String objectName = GrailsNameUtils.getPropertyName(property.rootBeanType)
        final String name = property.pathFromRoot
        Map attributes = [:]
        if (property.required) {
            attributes.required = null
        }
        if (property.constraints && !property.constraints.editable) {
            attributes.readonly = null
        }
        attributes["ng-model"] = "$controllerName.$objectName.$name"
        attributes.name = name
        attributes.id = name
        attributes
    }

    protected buildPropertyPath(DomainProperty property, Boolean includeControllerName) {
        StringBuilder sb = new StringBuilder()
        if (includeControllerName) {
            sb.append(controllerName).append('.')
        }
        sb.append(GrailsNameUtils.getPropertyName(property.rootBeanType)).append('.')
        sb.append(property.pathFromRoot)
        sb.toString()
    }

    Closure renderPropertyDisplay(DomainProperty property, Boolean includeControllerName) {
        return { ->
            span("{{${buildPropertyPath(property, includeControllerName)}}}")
        }
    }

    Closure renderAssociationDisplay(DomainProperty property, Boolean includeControllerName) {
        final String propertyPath = buildPropertyPath(property, includeControllerName)
        final String propertyName = GrailsNameUtils.getPropertyName(property.type)
        return { ->
            a("{{${propertyPath}.toString()}}", ["ui-sref": "${propertyName}.show({id: ${propertyPath}.id})"])
        }
    }

    Closure renderProperty(DomainProperty property) {
        PropertyType elementType = domainModelService.getPropertyType(property)
        switch(elementType) {
            case PropertyType.STRING:
                renderString(property)
                break
            case PropertyType.BOOLEAN:
                renderBoolean(property)
                break
            case PropertyType.NUMBER:
                renderNumber(property)
                break
            case PropertyType.URL:
                renderURL(property)
                break
            case PropertyType.ENUM:
                renderSelect(property)
                break
            case PropertyType.ASSOCIATION:
                renderAssociation(property)
                break
            case PropertyType.ONETOMANY:
                // TODO case oneToMany
                { -> }
                break
            case PropertyType.DATE:
                renderDate(property)
                break
            case PropertyType.TIME:
                renderTime(property)
                break
            case PropertyType.FILE:
                renderFile(property)
                break
            case PropertyType.TIMEZONE:
                renderTimeZone(property)
                break
            case PropertyType.CURRENCY:
                renderCurrency(property)
                break
            case PropertyType.LOCALE:
                renderLocale(property)
                break
            default:
                { -> }
        }
    }

    Closure renderString(DomainProperty property) {
        Constrained constraint = property.constraints
        if (constraint?.inList) {
            renderSelect(property)
        } else {
            if (property.constraints?.widget == "textarea") {
                renderTextArea(property)
            } else {
                renderInput(property)
            }
        }
    }

    Closure renderInput(DomainProperty property) {
        Map attributes = getStandardAttributes(property)
        if (property.constraints?.password) {
            attributes.type = "password"
        } else if (property.constraints?.email)  {
            attributes.type = "email"
        } else if (property.constraints?.url) {
            attributes.type = "url"
        } else {
            attributes.type = "text"
        }

        if (property.constraints?.matches) {
            attributes.pattern = property.constraints.matches
        }
        if (property.constraints?.maxSize) {
            attributes.maxlength = property.constraints.maxSize
        }

        return { ->
            input(attributes)
        }
    }

    Closure renderBoolean(DomainProperty property) {
        renderSimpleInput(property, "checkbox")
    }

    Closure renderNumber(DomainProperty property) {
        Map attributes = getStandardAttributes(property)
        Constrained constraint = property.constraints
        if (constraint?.inList) {
            renderSelect(property)
        } else {
            if (property.constraints?.range) {
                attributes.type = "range"
                attributes.min = property.constraints.range.from
                attributes.max = property.constraints.range.to
            } else {
                String typeName = property.type.simpleName.toLowerCase()

                attributes.type = "number"

                if(typeName in domainModelService.decimalTypes) {
                    attributes.step = "any"
                }
                if (property.constraints?.scale != null) {
                    attributes.step = "0.${'0' * (property.constraints.scale - 1)}1"
                }
                if (property.constraints?.min != null) {
                    attributes.min = property.constraints.min
                }
                if (property.constraints?.max != null) {
                    attributes.max = property.constraints.max
                }
            }
        }

        return { ->
            input(attributes)
        }
    }

    Closure renderURL(DomainProperty property) {
        renderSimpleInput(property, "url")
    }

    Closure renderSelect(DomainProperty property) {
        List inList
        List<Map> enumList = []
        Map attributes = getStandardAttributes(property)

        inList = property.constraints?.inList
        if (property.type.isEnum()) {
            List keys = property.type.values()*.name()
            List values = property.type.values()
            keys.eachWithIndex { k, i ->
                enumList.add([id: k, name: values[i].toString()])
            }
        }

        final String name = attributes.name

        if (inList) {
            attributes['ng-options'] = "$name for $name in ${JsonOutput.toJson(inList)}"
        } else if (enumList) {
            attributes['ng-options'] = "${name}.id as ${name}.name for ${name} in ${JsonOutput.toJson(enumList)}"
        } else {
            attributes['ng-options'] = "$name for $name in ${controllerName}.${name}List"
        }

        return { ->
            setDoubleQuotes(false)
            select('', attributes)
            setDoubleQuotes(true)
        }
    }

    Closure renderAssociation(DomainProperty property) {
        Map attributes = getStandardAttributes(property)
        final String name = attributes.name
        attributes['ng-options'] = "${name} as $name for $name in ${controllerName}.${name}List track by ${name}.id"

        if (property.property instanceof ManyToMany) {
            attributes["multiple"] = ""
        }

        return { ->
            select('', attributes)
        }
    }

    Closure renderTextArea(DomainProperty property) {
        Map attributes = getStandardAttributes(property)
        if (property.constraints?.maxSize) {
            attributes.maxlength = property.constraints.maxSize
        }
        return { ->
            textarea(attributes)
        }
    }

    Closure renderDate(DomainProperty property) {
        renderSimpleInput(property, "date")
    }

    Closure renderTime(DomainProperty property) {
        renderSimpleInput(property, "datetime-local")
    }

    Closure renderFile(DomainProperty property) {
        Map attributes = getStandardAttributes(property)
        attributes.type = "file"
        attributes['file-model'] = attributes.remove('ng-model')
        return { ->
            input(attributes)
        }
    }

    Closure renderTimeZone(DomainProperty property) {
        Map attributes = getStandardAttributes(property)
        String selected = TimeZone.default.ID

        attributes['ng-init'] = "${attributes["ng-model"]} = '$selected'"
        attributes['ng-options'] = "key as value for (key , value) in ${controllerName}.timeZoneList"

        return { ->
            select('', attributes)
        }
    }

    Closure renderCurrency(DomainProperty property) {
        Map attributes = getStandardAttributes(property)

        Currency currency = Currency.getInstance(Locale.default)
        String selected = currency.currencyCode

        attributes['ng-init'] = "${attributes["ng-model"]} = '$selected'"
        final String name = attributes.name
        attributes['ng-options'] = "$name for $name in ${controllerName}.currencyList"

        return { ->
            select('', attributes)
        }
    }

    Closure renderLocale(DomainProperty property) {
        Map attributes = getStandardAttributes(property)

        Locale locale = Locale.default
        String selected = locale.country ? "${locale.language}_${locale.country}" : locale.language

        attributes['ng-init'] = "${attributes["ng-model"]} = '$selected'"
        attributes['ng-options'] = "key as value for (key , value) in ${controllerName}.localeList"

        return { ->
            select('', attributes)
        }
    }

    private Closure renderSimpleInput(DomainProperty property, String type) {
        Map attributes = getStandardAttributes(property)
        attributes.type = type
        return { ->
            input(attributes)
        }
    }
}
