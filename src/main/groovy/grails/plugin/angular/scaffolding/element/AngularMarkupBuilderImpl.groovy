package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor
import grails.util.GrailsNameUtils
import grails.validation.ConstrainedProperty
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Value
import java.sql.Blob

class AngularMarkupBuilderImpl implements AngularMarkupBuilder {

    @Value('${grails.plugin.angular.scaffolding.controllerName:vm}')
    String controllerName

    private static final List<String> DECIMAL_TYPES = ['double', 'float', 'bigdecimal']

    PropertyType getPropertyType(BeanPropertyAccessor property) {
        if (property.propertyType in [String, null]) {
            PropertyType.STRING
        } else if (property.propertyType in [boolean, Boolean]) {
            PropertyType.BOOLEAN
        } else if (property.propertyType.isPrimitive() || property.propertyType in Number) {
            PropertyType.NUMBER
        } else if (property.propertyType in URL) {
            PropertyType.URL
        } else if (property.propertyType.isEnum()) {
            PropertyType.ENUM
        } else if (property.persistentProperty?.oneToOne || property.persistentProperty?.manyToOne || property.persistentProperty?.manyToMany) {
            PropertyType.ASSOCIATION
        } else if (property.persistentProperty?.oneToMany) {
            PropertyType.ONETOMANY
        } else if (property.propertyType in [Date, Calendar, java.sql.Date]) {
            PropertyType.DATE
        } else if (property.propertyType in java.sql.Time) {
            PropertyType.TIME
        } else if (property.propertyType in [byte[], Byte[], Blob]) {
            PropertyType.FILE
        } else if (property.propertyType in [TimeZone, Currency, Locale]) {
            PropertyType.SPECIAL
        }
    }

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
        if (getPropertyType(property) == PropertyType.ENUM) {
            sb.append(".name")
        }
        "{{${sb.toString()}}}"
    }

    Closure renderProperty(BeanPropertyAccessor property) {
        PropertyType elementType = getPropertyType(property)
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
                // TODO case timezone,currency,locale
                { -> }
                break
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
                enumList.add([id: k, name: values[i]])
            }
        }

        final String name = attributes.name

        if (inList) {
            attributes['ng-options'] = "$name for $name in ${JsonOutput.toJson(inList)}"
        } else if (enumList) {
            attributes['ng-options'] = "${name}.id as ${name}.name for ${name} in ${JsonOutput.toJson(enumList)}"
        } else {
            attributes['ng-options'] = "$name for $name in ${name}s"
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

    private Closure renderSimpleInput(BeanPropertyAccessor property, String type) {
        Map attributes = getStandardAttributes(property)
        attributes.type = type
        return { ->
            input(attributes)
        }
    }
}
