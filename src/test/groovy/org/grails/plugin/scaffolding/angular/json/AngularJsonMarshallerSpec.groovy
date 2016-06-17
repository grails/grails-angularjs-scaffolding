package org.grails.plugin.scaffolding.angular.json

import grails.converters.JSON
import org.grails.web.converters.marshaller.json.MapMarshaller
import spock.lang.Specification
import spock.lang.Subject


@Subject(AngularJsonMarshaller)
class AngularJsonMarshallerSpec extends Specification {

    void setup() {
        JSON.registerObjectMarshaller(new MapMarshaller())
        new AngularJsonMarshaller().appendMarshallers()
    }

    void "test timeZone rendering"() {
        given:
        JSON json = new JSON()
        json.setTarget([timeZone: TimeZone.getTimeZone("America/New_York")])

        when:
        String jsonString = json.toString()

        then:
        jsonString == '{"timeZone":"America/New_York"}'
    }

    void "test byte array rendering"() {
        given:
        JSON json = new JSON()
        json.setTarget([bytes: "abc".bytes])

        when:
        String jsonString = json.toString()

        then:
        jsonString ==~ /\{"bytes":"\[B@(.*?)"\}/
    }

    void "test enum rendering"() {
        given:
        JSON json = new JSON()
        json.setTarget([enum: Fruits.ORANGE, namedEnum: NamedFruits.APPLE])

        when:
        String jsonString = json.toString()

        then:
        jsonString == '{"enum":"ORANGE","namedEnum":"APPLE"}'
    }

    enum Fruits {
        ORANGE, APPLE
    }

    enum NamedFruits {
        ORANGE("Orange"), APPLE("Apple")
        private final String val
        NamedFruits(String val) {
            this.val = val
        }
    }
}
