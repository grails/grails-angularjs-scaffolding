package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty

/**
 * Created by Jim on 5/25/2016.
 */
trait MapToSelectInputRenderer<T> {

    abstract String getOptionValue(T t)

    abstract String getOptionKey(T t)

    abstract T getDefaultOption()

    abstract Map<String, String> getOptions()

    Closure renderInput(Map defaultAttributes, DomainProperty property) {
        String selected = getOptionKey(defaultOption)

        return { ->
            select(defaultAttributes) {
                options.each { String key, String value ->
                    Map attrs = [value: key]
                    if (selected == key) {
                        attrs.selected = ""
                    }
                    option(value, attrs)
                }
            }
        }
    }

}