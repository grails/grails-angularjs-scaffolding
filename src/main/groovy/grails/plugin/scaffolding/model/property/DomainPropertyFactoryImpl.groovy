package grails.plugin.scaffolding.model.property

import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.sql.Blob

/**
 * Created by Jim on 5/20/2016.
 */
class DomainPropertyFactoryImpl implements DomainPropertyFactory {

    @Value('${grails.databinding.convertEmptyStringsToNull:true}')
    Boolean convertEmptyStringsToNull

    @Value('${grails.databinding.trimStrings:true}')
    Boolean trimStrings

    @Autowired
    MappingContext grailsDomainClassMappingContext

    DomainProperty build(PersistentProperty persistentProperty) {
        DomainPropertyImpl domainProperty = new DomainPropertyImpl(persistentProperty, grailsDomainClassMappingContext)
        init(domainProperty)
        domainProperty
    }

    DomainProperty build(PersistentProperty rootProperty, PersistentProperty persistentProperty) {
        DomainPropertyImpl domainProperty = new DomainPropertyImpl(rootProperty, persistentProperty, grailsDomainClassMappingContext)
        init(domainProperty)
        domainProperty
    }

    private init(DomainPropertyImpl domainProperty) {
        domainProperty.convertEmptyStringsToNull = convertEmptyStringsToNull
        domainProperty.trimStrings = trimStrings
    }
}
