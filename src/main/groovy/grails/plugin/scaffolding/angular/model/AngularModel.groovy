package grails.plugin.scaffolding.angular.model

import grails.codegen.model.Model

/**
 * Created by Jim on 5/18/2016.
 */
interface AngularModel extends Model {

    String getModuleName()

    String getModulePath()

    AngularModel getParentModule()

    File getFile()

    Boolean exists()
}
