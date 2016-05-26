package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/24/2016.
 */
class EnumInputRenderer implements DomainInputRenderer {

    protected List<Map> getEnumValues(DomainProperty property) {
        List<Map> enumList = []
        List keys = property.type.values()*.name()
        List values = property.type.values()
        keys.eachWithIndex { k, i ->
            enumList.add([id: k, name: values[i].toString()])
        }
        enumList
    }

    @Override
    boolean supports(DomainProperty property) {
        property.type.isEnum()
    }

    @Override
    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        List<Map> enumList = getEnumValues(property)

        return { ->
            select(defaultAttributes) {
                enumList.each {
                    option(it.name, [value: it.id])
                }
            }
        }
    }
}
