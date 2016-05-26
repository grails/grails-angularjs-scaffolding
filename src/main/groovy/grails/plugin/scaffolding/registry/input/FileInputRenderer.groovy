package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

import java.sql.Blob

/**
 * Created by Jim on 5/24/2016.
 */
class FileInputRenderer implements DomainInputRenderer {

    @Override
    boolean supports(DomainProperty property) {
        property.type in [byte[], Byte[], Blob]
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        defaultAttributes.type = "file"
        return { ->
            input(defaultAttributes)
        }
    }
}
