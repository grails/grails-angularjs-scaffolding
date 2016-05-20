package grails.plugin.angular.scaffolding.renderers

import grails.converters.JSON
import grails.test.mixin.integration.Integration
import spock.lang.Specification

@Integration
class AngularJsonRendererSpec extends Specification {

    void "test TimeZone marshalling"() {
        expect:
        ([timeZone: TimeZone.getTimeZone("America/New_York")] as JSON).toString() == '{"timeZone":"America/New_York"}'
    }

    void "test byte[] marshalling"() {
        expect:
        ([data: "abc".bytes] as JSON).toString() ==~ /\{"data":"\[B.*"\}/
    }

    void "test enum rendering"() {
        expect:
        ([enum: TestEnum.B] as JSON).toString() == '{"enum":"B"}'
    }

    enum TestEnum {
        A("a"),
        B("b")
        final String value
        TestEnum(String value) {
            this.value = value
        }
        String toString() {
            this.value
        }
    }
}
