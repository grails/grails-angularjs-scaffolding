package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/24/2016.
 */
class TimeZoneInputRenderer implements DomainInputRenderer, MapToSelectInputRenderer<TimeZone> {

    String getOptionValue(TimeZone timeZone) {
        Date date = new Date()
        String shortName = timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.SHORT)
        String longName = timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.LONG)

        int offset = timeZone.rawOffset
        def hour = offset / (60 * 60 * 1000)
        def min = Math.abs(offset / (60 * 1000)) % 60

        "${shortName}, ${longName} ${hour}:${min} [${timeZone.ID}]"
    }

    String getOptionKey(TimeZone timeZone) {
        timeZone.ID
    }

    Map<String, String> getOptions() {
        TimeZone.availableIDs.collectEntries {
            TimeZone timeZone = TimeZone.getTimeZone(it)
            [(getOptionKey(timeZone)): getOptionValue(timeZone)]
        }
    }

    TimeZone getDefaultOption() {
        TimeZone.default
    }

    @Override
    boolean supports(DomainProperty property) {
        property.type in TimeZone
    }

}
