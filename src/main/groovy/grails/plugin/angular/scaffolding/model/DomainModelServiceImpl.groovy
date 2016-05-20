package grails.plugin.angular.scaffolding.model

import grails.plugin.angular.scaffolding.model.property.PropertyType
import grails.plugin.angular.scaffolding.model.property.DomainProperty
import grails.plugin.angular.scaffolding.model.property.DomainPropertyFactory
import grails.util.GrailsClassUtils
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Embedded
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Blob

class DomainModelServiceImpl implements DomainModelService {

    @Autowired
    MappingContext grailsDomainClassMappingContext

    @Autowired
    DomainPropertyFactory domainPropertyFactory

    List<String> currencyCodes = ['EUR', 'XCD', 'USD', 'XOF', 'NOK', 'AUD',
                                  'XAF', 'NZD', 'MAD', 'DKK', 'GBP', 'CHF',
                                  'XPF', 'ILS', 'ROL', 'TRL']

    List<String> decimalTypes = ['double', 'float', 'bigdecimal']

    List<DomainProperty> getEditableProperties(PersistentEntity domainClass) {
        List<DomainProperty> properties = domainClass.persistentProperties.collect {
            domainPropertyFactory.build(it)
        }
        List blacklist = ['version', 'dateCreated', 'lastUpdated']
        def scaffoldProp = GrailsClassUtils.getStaticPropertyValue(domainClass.javaClass, 'scaffold')
        if (scaffoldProp) {
            blacklist.addAll(scaffoldProp.exclude)
        }
        properties.removeAll { it.name in blacklist }
        properties.removeAll { !it.constraints.display }
        //TODO Wait for Graeme to implement access to determine if a property is derived
        //properties.removeAll { it.mapping instanceof PropertyConfig classMapping.mappedForm..properties.derived }
        properties.sort()
        properties
    }

    List<DomainProperty> getVisibleProperties(PersistentEntity domainClass) {
        List<DomainProperty> properties = domainClass.persistentProperties.collect {
            domainPropertyFactory.build(it)
        }
        properties.removeAll { it.name == 'version' }
        properties.sort()
        properties
    }



    List<PersistentProperty> getShortListVisibleProperties(PersistentEntity domainClass) {
        List<PersistentProperty> properties = getVisibleProperties(domainClass)
        if (properties.size() > 6) {
            properties = properties[0..6]
        }
        properties
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

    protected Boolean isTimeZone(Class clazz) {
        clazz in TimeZone
    }

    protected Boolean isCurrency(Class clazz) {
        clazz in Currency
    }

    protected Boolean isLocale(Class clazz) {
        clazz in Locale
    }

    PropertyType getPropertyType(DomainProperty property) {
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
        } else if (property.property instanceof OneToOne || property.property instanceof ManyToOne || property.property instanceof ManyToMany) {
            PropertyType.ASSOCIATION
        } else if (property.property instanceof OneToMany) {
            PropertyType.ONETOMANY
        } else if (isDate(property.type)) {
            PropertyType.DATE
        } else if (isTime(property.type)) {
            PropertyType.TIME
        } else if (isFile(property.type)) {
            PropertyType.FILE
        } else if (isTimeZone(property.type)) {
            PropertyType.TIMEZONE
        } else if (isCurrency(property.type)) {
            PropertyType.CURRENCY
        } else if (isLocale(property.type)) {
            PropertyType.LOCALE
        }
    }

    protected Boolean hasProperty(PersistentEntity domainClass, Closure closure) {
        getEditableProperties(domainClass).any { DomainProperty domainProperty ->
            PersistentProperty property = domainProperty.property
            if (property instanceof Embedded) {
                hasProperty(property.associatedEntity, closure)
            } else {
                closure.call(domainProperty)
            }
        }
    }

    Boolean hasPropertyType(PersistentEntity domainClass, PropertyType propertyType) {
        hasProperty(domainClass) { DomainProperty property ->
            getPropertyType(property) == propertyType
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

    protected String formatLocale(Locale locale) {
        locale.country ? "${locale.language}, ${locale.country},  ${locale.displayName}" : "${locale.language}, ${locale.displayName}"
    }

    Map<String, String> getTimeZones() {
        TimeZone.availableIDs.collectEntries {
            [(it): formatTimeZone(TimeZone.getTimeZone(it))]
        }
    }

    Map<String, String> getLocales() {
        Locale.availableLocales.collectEntries {
            if (it.country || it.language) {
                String key = it.country ? "${it.language}_${it.country}" : it.language
                [(key): formatLocale(it)]
            } else {
                [:]
            }
        }
    }
}
