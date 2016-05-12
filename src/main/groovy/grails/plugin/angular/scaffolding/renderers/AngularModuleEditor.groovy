package grails.plugin.angular.scaffolding.renderers

import grails.codegen.model.Model

interface AngularModuleEditor {

    boolean addDependency(File module, String dependency)

    boolean addRequire(File module, String require)

    boolean addDependency(File module, Model model)
}