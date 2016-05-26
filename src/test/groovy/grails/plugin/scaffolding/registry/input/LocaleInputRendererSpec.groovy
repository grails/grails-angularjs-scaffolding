package grails.plugin.scaffolding.registry.input

import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Jim on 5/26/2016.
 */
class LocaleInputRendererSpec extends Specification {

    @Shared
    LocaleInputRenderer renderer

    void setup() {
        renderer = new LocaleInputRenderer()
    }

    void "test option key and value"() {
        given:
        Locale locale

        when:
        locale = Locale.US

        then:
        renderer.getOptionKey(locale) == "en_US"
        renderer.getOptionValue(locale) == "en, US,  English (United States)"

        when:
        locale = Locale.ENGLISH

        then:
        renderer.getOptionKey(locale) == "en"
        renderer.getOptionValue(locale) == "en, English"
    }
}
