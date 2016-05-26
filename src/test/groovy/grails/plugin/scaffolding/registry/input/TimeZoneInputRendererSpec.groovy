package grails.plugin.scaffolding.registry.input

import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Jim on 5/26/2016.
 */
class TimeZoneInputRendererSpec extends Specification {

    @Shared
    TimeZoneInputRenderer renderer

    void setup() {
        renderer = new TimeZoneInputRenderer()
    }

    void "test option key and value"() {
        given:
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York")

        expect:
        renderer.getOptionKey(timeZone) == "America/New_York"
        renderer.getOptionValue(timeZone) == "EDT, Eastern Daylight Time -5:0.0 [America/New_York]"
    }
}
