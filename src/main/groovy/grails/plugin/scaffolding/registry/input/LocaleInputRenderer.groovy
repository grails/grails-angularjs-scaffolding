package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer
import groovy.transform.CompileStatic

/**
 * The default renderer for rendering {@link Locale} properties
 *
 * @author James Kleeh
 */
@CompileStatic
class LocaleInputRenderer implements MapToSelectInputRenderer<Locale> {

    String getOptionValue(Locale locale) {
        locale.country ? "${locale.language}, ${locale.country},  ${locale.displayName}" : "${locale.language}, ${locale.displayName}"
    }

    String getOptionKey(Locale locale) {
        locale.country ? "${locale.language}_${locale.country}" : locale.language
    }

    Map<String, String> getOptions() {
        Locale.availableLocales.collectEntries {
            if (it.country || it.language) {
                [(getOptionKey(it)): getOptionValue(it)]
            } else {
                [:]
            }
        }
    }

    Locale getDefaultOption() {
        Locale.default
    }

    @Override
    boolean supports(DomainProperty property) {
        property.type in Locale
    }

}
