package org.grails.plugin.scaffolding.angular.markup

import org.grails.scaffolding.markup.ContextMarkupRenderer

/**
 * Created by Jim on 5/26/2016.
 */
interface AngularContextMarkupRenderer extends ContextMarkupRenderer {

    String getControllerName()

}
