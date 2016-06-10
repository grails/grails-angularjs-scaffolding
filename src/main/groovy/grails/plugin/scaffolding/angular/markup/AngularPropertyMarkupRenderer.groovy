package grails.plugin.scaffolding.angular.markup

import org.grails.scaffolding.markup.PropertyMarkupRenderer

/**
 * Created by Jim on 5/26/2016.
 */
interface AngularPropertyMarkupRenderer extends PropertyMarkupRenderer {

    String getControllerName()

}
