package grails.plugin.angular.scaffolding.element

import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.formfields.BeanPropertyAccessor
import grails.util.GrailsNameUtils
import grails.validation.ConstrainedProperty
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.sql.Blob

class AngularMarkupBuilderImpl implements AngularMarkupBuilder {

    @Autowired
    DomainModelService domainModelService

    @Value('${grails.plugin.angular.scaffolding.controllerName:vm}')
    String controllerName

    private static final List<String> DECIMAL_TYPES = ['double', 'float', 'bigdecimal']

    Map getStandardAttributes(BeanPropertyAccessor property) {
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

    String renderPropertyDisplay(BeanPropertyAccessor property, Boolean includeControllerName) {
        StringBuilder sb = new StringBuilder()
        if (includeControllerName) {
            sb.append(controllerName).append('.')
        }
        sb.append(GrailsNameUtils.getPropertyName(property.rootBeanType)).append('.')
        sb.append(property.pathFromRoot)
        "{{${sb.toString()}}}"
    }

    Closure renderProperty(BeanPropertyAccessor property) {
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
                // TODO case association
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
            case PropertyType.SPECIAL:
                if (property.propertyType == TimeZone) {
                    renderTimeZone(property)
                } else {
                    // TODO case currency,locale
                    { -> }
                }
                break
            default:
                { -> }
        }
    }

    Closure renderString(BeanPropertyAccessor property) {
        ConstrainedProperty constraint = property.constraints
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

    Closure renderInput(BeanPropertyAccessor property) {
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

        { ->
            input(attributes)
        }
    }

    Closure renderBoolean(BeanPropertyAccessor property) {
        renderSimpleInput(property, "checkbox")
    }

    Closure renderNumber(BeanPropertyAccessor property) {
        Map attributes = getStandardAttributes(property)
        ConstrainedProperty constraint = property.constraints
        if (constraint?.inList) {
            renderSelect(property)
        } else {
            if (property.constraints?.range) {
                attributes.type = "range"
                attributes.min = property.constraints.range.from
                attributes.max = property.constraints.range.to
            } else {
                String typeName = property.propertyType.simpleName.toLowerCase()

                if(typeName in DECIMAL_TYPES) {
                    attributes.type = "number decimal"
                } else {
                    attributes.type = "number"
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

        { ->
            input(attributes)
        }
    }

    Closure renderURL(BeanPropertyAccessor property) {
        renderSimpleInput(property, "url")
    }

    Closure renderSelect(BeanPropertyAccessor property) {
        List inList
        List<Map> enumList = []
        Map attributes = getStandardAttributes(property)

        inList = property.constraints?.inList
        if (property.propertyType.isEnum()) {
            List keys = property.propertyType.values()*.name()
            List values = property.propertyType.values()
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

        { ->
            setDoubleQuotes(false)
            select('', attributes)
            setDoubleQuotes(true)
        }
    }

    Closure renderTextArea(BeanPropertyAccessor property) {
        Map attributes = getStandardAttributes(property)
        if (property.constraints?.maxSize) {
            attributes.maxlength = property.constraints.maxSize
        }
        { ->
            textarea(attributes)
        }
    }

    Closure renderDate(BeanPropertyAccessor property) {
        renderSimpleInput(property, "date")
    }

    Closure renderTime(BeanPropertyAccessor property) {
        renderSimpleInput(property, "datetime-local")
    }

    Closure renderFile(BeanPropertyAccessor property) {
        Map attributes = getStandardAttributes(property)
        attributes.type = "file"
        attributes['file-model'] = attributes.remove('ng-model')
        return { ->
            input(attributes)
        }
    }

    Closure renderTimeZone(BeanPropertyAccessor property) {
        Map attributes = getStandardAttributes(property)
        String selected = TimeZone.default.ID

        attributes['ng-init'] = "${attributes["ng-model"]} = '$selected'"
        final String name = attributes.name
        attributes['ng-options'] = "$name for $name in $controllerName.${name}List"

        return { ->
            select('', attributes)
        }
    }

    private Closure renderSimpleInput(BeanPropertyAccessor property, String type) {
        Map attributes = getStandardAttributes(property)
        attributes.type = type
        return { ->
            input(attributes)
        }
    }
}
