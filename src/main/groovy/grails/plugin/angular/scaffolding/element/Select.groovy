package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor
import groovy.json.JsonBuilder

/**
 * Created by Jim on 5/4/2016.
 */
class Select extends Element {

    List inList
    List<Map> enumList = []

    Select(BeanPropertyAccessor property) {
        super(property)
        inList = property.constraints?.inList
        if (property.propertyType.isEnum()) {
            List keys = property.propertyType.values()*.name()
            List values = property.propertyType.values()
            keys.eachWithIndex { k, i ->
                enumList.add([id: k, name: values[i]])
            }
        }

    }

    String buildNgOptions() {
        if (inList) {
            "$name for $name in ${new JsonBuilder(inList).toString()}"
        } else if (enumList) {
            "${name}.id as ${name}.name for ${name} in ${new JsonBuilder(enumList).toString()}"
        } else {
            "$name for $name in ${name}s"
        }
    }

    @Override
    String render() {
        "<select ng-options='${buildNgOptions()}' ${renderOtherAttributes()}></select>"
    }
}
