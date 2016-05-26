package grails.plugin.angular.scaffolding.element

import grails.gorm.annotation.Entity
import grails.plugin.scaffolding.ClosureCapture
import grails.plugin.scaffolding.markup.PropertyMarkupRenderer
import grails.plugin.scaffolding.model.DomainModelService
import grails.plugin.scaffolding.model.DomainModelServiceImpl
import grails.plugin.scaffolding.model.MocksDomain
import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.model.property.DomainPropertyFactory
import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Jim on 5/20/2016.
 */
@Ignore
class AngularMarkupBuilderSpec extends Specification implements MocksDomain {
/*
    @Shared
    PropertyMarkupRenderer angularMarkupBuilder

    @Shared
    MappingContext mappingContext

    @Shared
    DomainPropertyFactory domainPropertyFactory

    void setup() {
        mappingContext = new KeyValueMappingContext("test")
        domainPropertyFactory = mockDomainPropertyFactory(mappingContext)
        DomainModelService domainModelService = new DomainModelServiceImpl(grailsDomainClassMappingContext: mappingContext, domainPropertyFactory: domainPropertyFactory)
        angularMarkupBuilder = new PropertyMarkupRendererImpl(controllerName: "vm", domainModelService: domainModelService)
    }

    void "test getStandardAttributes"() {
        given:
        PersistentEntity author = mockDomainClass(mappingContext, Author)
        Map standardAttributes

        when:
        DomainProperty name = domainPropertyFactory.build(author.persistentProperties.find { it.name == "name" })
        standardAttributes = angularMarkupBuilder.getStandardAttributes(name)

        then:
        standardAttributes == [required: null, "ng-model": "vm.author.name", name: "name", id: "name"]

        when:
        DomainProperty birthDay = domainPropertyFactory.build(author.persistentProperties.find { it.name == "birthDay" })
        standardAttributes = angularMarkupBuilder.getStandardAttributes(birthDay)

        then:
        standardAttributes == [readonly: null, "ng-model": "vm.author.birthDay", name: "birthDay", id: "birthDay"]
    }

    void "test renderPropertyDisplay"() {
        given:
        PersistentEntity author = mockDomainClass(mappingContext, Author)
        ClosureCapture capture = new ClosureCapture()

        when:
        DomainProperty books = domainPropertyFactory.build(author.persistentProperties.find { it.name == "books" })
        Closure markup = angularMarkupBuilder.renderPropertyDisplay(books, true)
        markup.delegate = capture
        markup.call()

        then:
        capture.calls[0].name == "ul"
        capture.calls[0].args[0] == ["ng-repeat":"book in vm.author.books"]
        capture.calls[0].calls[0].name == "li"
        capture.calls[0].calls[0].calls[0].name == "a"
        capture.calls[0].calls[0].calls[0].args[0] == "{{book.toString()}}"
        capture.calls[0].calls[0].calls[0].args[1] == ["ui-sref": "book.show({id: book.id})"]
    }*/
}

@Entity
class Author {
    String name
    Date birthDay
    static hasMany = [books: Book]
    static constraints = {
        birthDay editable: false, nullable: true
    }
}

@Entity
class Book {
    String title
}