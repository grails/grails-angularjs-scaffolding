package grails.plugin.angular.scaffolding.element

import grails.plugin.formfields.BeanPropertyAccessor
import grails.util.GrailsNameUtils
import org.grails.encoder.CodecLookup
import org.grails.encoder.Encoder

/**
 * Created by Jim on 5/4/2016.
 */
abstract class Element {

    String name
    String objectName
    Map otherAttributes = [:]
    static CodecLookup codecLookup

    Element(BeanPropertyAccessor property) {
        this.objectName = GrailsNameUtils.getPropertyName(property.rootBeanType)
        this.name = property.pathFromRoot
        if (property.required) {
            otherAttributes.required = null
        }
        if (property.constraints && !property.constraints.editable) {
            otherAttributes.readonly = null
        }
        otherAttributes["ng-model"] = "$controllerName.$objectName.$name"
        otherAttributes.name = name
        otherAttributes.id = name
    }

    abstract String render()

    String getControllerName() {
        "vm"
    }

    String renderDisplay() {
        "{{$controllerName.$objectName.$name}}"
    }

    String renderOtherAttributes() {
        StringBuilder sb = new StringBuilder()
        Encoder htmlEncoder = codecLookup?.lookupEncoder('HTML')
        otherAttributes.each { k, v ->
            if (v != null) {
                sb.append("$k=\"${htmlEncoder != null ? htmlEncoder.encode(v) : v}\" ")
            } else {
                sb.append("$k ")
            }
        }
        sb.toString()
    }


}
