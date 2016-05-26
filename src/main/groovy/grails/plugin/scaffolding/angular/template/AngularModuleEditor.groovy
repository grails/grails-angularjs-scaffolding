package grails.plugin.scaffolding.angular.template

import grails.codegen.model.Model

interface AngularModuleEditor {

    boolean addDependency(File module, String dependency)

    boolean addRequire(File module, String require)

    boolean addDependency(File module, Model model)
}