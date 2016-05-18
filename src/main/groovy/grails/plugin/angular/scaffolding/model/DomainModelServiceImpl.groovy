package grails.plugin.angular.scaffolding.model

import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.element.PropertyType
import grails.plugin.formfields.BeanPropertyAccessor
import grails.util.GrailsClassUtils
import org.grails.validation.DomainClassPropertyComparator

import java.sql.Blob

class DomainModelServiceImpl implements DomainModelService {

    List<GrailsDomainClassProperty> getEditableProperties(GrailsDomainClass domainClass) {
        List<GrailsDomainClassProperty> properties = domainClass.persistentProperties as List
        List blacklist = ['dateCreated', 'lastUpdated']
        def scaffoldProp = GrailsClassUtils.getStaticPropertyValue(domainClass.clazz, 'scaffold')
        if (scaffoldProp) {
            blacklist.addAll(scaffoldProp.exclude)
        }
        properties.removeAll { it.name in blacklist }
        properties.removeAll { !it.domainClass.constrainedProperties[it.name]?.display }
        properties.removeAll { it.derived }

        sort(properties, domainClass)
        properties
    }

    List<GrailsDomainClassProperty> getVisibleProperties(GrailsDomainClass domainClass) {
        List<GrailsDomainClassProperty> properties = domainClass.persistentProperties
        sort(properties, domainClass)
        if (properties.size() > 6) {
            properties = properties[0..6]
        }
        properties
    }

    void sort(List<GrailsDomainClassProperty> properties, GrailsDomainClass domainClass) {
        Collections.sort(properties, new DomainClassPropertyComparator(domainClass))
    }
    
    protected Boolean isString(Class clazz) {
        clazz in [String, null]
    }

    protected Boolean isBoolean(Class clazz) {
        clazz in [boolean, Boolean]
    }

    protected Boolean isNumber(Class clazz) {
        clazz.isPrimitive() || clazz in Number
    }

    protected Boolean isURL(Class clazz) {
        clazz in URL
    }

    protected Boolean isEnum(Class clazz) {
        clazz.isEnum()
    }

    protected Boolean isDate(Class clazz) {
        clazz in [Date, Calendar, java.sql.Date]
    }

    protected Boolean isTime(Class clazz) {
        clazz in java.sql.Time
    }

    protected Boolean isFile(Class clazz) {
        clazz in [byte[], Byte[], Blob]
    }

    protected Boolean isSpecial(Class clazz) {
        clazz in [TimeZone, Currency, Locale]
    }

    PropertyType getPropertyType(BeanPropertyAccessor property) {
        if (isString(property.propertyType)) {
            PropertyType.STRING
        } else if (isBoolean(property.propertyType)) {
            PropertyType.BOOLEAN
        } else if (isNumber(property.propertyType)) {
            PropertyType.NUMBER
        } else if (isURL(property.propertyType)) {
            PropertyType.URL
        } else if (isEnum(property.propertyType)) {
            PropertyType.ENUM
        } else if (property.persistentProperty?.oneToOne || property.persistentProperty?.manyToOne || property.persistentProperty?.manyToMany) {
            PropertyType.ASSOCIATION
        } else if (property.persistentProperty?.oneToMany) {
            PropertyType.ONETOMANY
        } else if (isDate(property.propertyType)) {
            PropertyType.DATE
        } else if (isTime(property.propertyType)) {
            PropertyType.TIME
        } else if (isFile(property.propertyType)) {
            PropertyType.FILE
        } else if (isSpecial(property.propertyType)) {
            PropertyType.SPECIAL
        }
    }

    PropertyType getPropertyType(GrailsDomainClassProperty property) {
        if (isString(property.type)) {
            PropertyType.STRING
        } else if (isBoolean(property.type)) {
            PropertyType.BOOLEAN
        } else if (isNumber(property.type)) {
            PropertyType.NUMBER
        } else if (isURL(property.type)) {
            PropertyType.URL
        } else if (isEnum(property.type)) {
            PropertyType.ENUM
        } else if (property.oneToOne || property.manyToOne || property.manyToMany) {
            PropertyType.ASSOCIATION
        } else if (property.oneToMany) {
            PropertyType.ONETOMANY
        } else if (isDate(property.type)) {
            PropertyType.DATE
        } else if (isTime(property.type)) {
            PropertyType.TIME
        } else if (isFile(property.type)) {
            PropertyType.FILE
        } else if (isSpecial(property.type)) {
            PropertyType.SPECIAL
        }
    }
    
    protected Boolean hasProperty(GrailsDomainClass domainClass, Closure closure) {
        getEditableProperties(domainClass).any {
            if (it.embedded) {
                hasProperty(it.component, closure)
            } else {
                closure.call(it)
            }
        }
    }
    
    Boolean hasFileProperty(GrailsDomainClass domainClass) {
        hasProperty(domainClass) { GrailsDomainClassProperty property ->
            isFile(property.type)
        }
    }
    
    Boolean hasTimeZoneProperty(GrailsDomainClass domainClass) {
        hasProperty(domainClass) { GrailsDomainClassProperty property ->
            property.type in TimeZone
        }
    }

    protected String formatTimeZone(TimeZone timeZone) {
        Date date = new Date()
        String shortName = timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.SHORT)
        String longName = timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.LONG)

        int offset = timeZone.rawOffset
        def hour = offset / (60 * 60 * 1000)
        def min = Math.abs(offset / (60 * 1000)) % 60

        "${shortName}, ${longName} ${hour}:${min} [${timeZone.ID}]"
    }

    Map<String, String> getTimeZones() {
        TimeZone.availableIDs.collectEntries {
            [(it): formatTimeZone(TimeZone.getTimeZone(it))]
        }
    }
}
