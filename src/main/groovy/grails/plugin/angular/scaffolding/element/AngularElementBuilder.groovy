package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor
import grails.util.GrailsNameUtils
import grails.validation.ConstrainedProperty
import groovy.json.JsonBuilder
import org.springframework.beans.factory.annotation.Value
import java.sql.Blob

class AngularElementBuilder {

    @Value('${grails.plugin.angular.scaffolding.controllerName:vm}')
    String controllerName

    private static final List<String> DECIMAL_TYPES = ['double', 'float', 'bigdecimal']


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

    Closure renderElement(BeanPropertyAccessor property) {
        ElementType elementType = getElementType(property)
        switch(elementType) {
            case ElementType.STRING:
                renderString(property)
                break
            case ElementType.BOOLEAN:
                renderBoolean(property)
                break
            case ElementType.NUMBER:
                renderNumber(property)
                break
            case ElementType.URL:
                renderURL(property)
                break
            case ElementType.ENUM:
                renderSelect(property)
                break
            case ElementType.ASSOCIATION:
                // TODO case association
            case ElementType.ONETOMANY:
                // TODO case oneToMany
                break
            case ElementType.DATE:
                renderDate(property)
                break
            case ElementType.TIME:
                renderTime(property)
                break
            case ElementType.FILE:
                // TODO case file
            case ElementType.SPECIAL:
                // TODO case timezone,currency,locale
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
            attributes['ng-options'] = "$name for $name in ${new JsonBuilder(inList).toString()}"
        } else if (enumList) {
            attributes['ng-options'] = "${name}.id as ${name}.name for ${name} in ${new JsonBuilder(enumList).toString()}"
        } else {
            attributes['ng-options'] = "$name for $name in ${name}s"
        }

        { ->
            select(attributes)
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

    private Closure renderSimpleInput(BeanPropertyAccessor property, String type) {
        Map attributes = getStandardAttributes(property)
        attributes.type = type
        return { ->
            input(attributes)
        }
    }
}
