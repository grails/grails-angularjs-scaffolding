package org.grails.plugin.scaffolding.angular

import org.grails.plugin.scaffolding.angular.json.AngularJsonMarshaller
import org.grails.plugin.scaffolding.angular.markup.AngularContextMarkupRendererImpl
import org.grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRendererImpl
import org.grails.plugin.scaffolding.angular.registry.AngularDomainRendererRegisterer
import org.grails.plugin.scaffolding.angular.template.AngularModuleEditorImpl
import grails.plugins.*
import org.grails.scaffolding.ScaffoldingBeanConfiguration

class GrailsAngularScaffoldingGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.0 > *"

    // TODO Fill in these fields
    def title = "Grails Angular Scaffolding" // Headline output name of the plugin
    def author = "James Kleeh"
    def authorEmail = "kleehj@ociweb.com"
    def description = '''\
This plugin provides the ability to generate an AngularJS CRUD interface based on a domain class
'''
    String documentation = 'http://grails-plugins.github.io/grails-angular-scaffolding/latest'
    String license = 'APACHE'
    def organization = [name: 'Grails', url: 'http://www.grails.org/']
    def issueManagement = [url: 'https://github.com/grails-plugins/grails-angular-scaffolding/issues']
    def scm = [url: 'https://github.com/grails-plugins/grails-angular-scaffolding']

    Closure doWithSpring() { {->
        scaffoldingCoreConfig(ScaffoldingBeanConfiguration)

        propertyMarkupRenderer(AngularPropertyMarkupRendererImpl)

        contextMarkupRenderer(AngularContextMarkupRendererImpl)

        angularModuleEditor(AngularModuleEditorImpl)

        angularJsonMarshaller(AngularJsonMarshaller)

        angulardDomainRendererRegisterer(AngularDomainRendererRegisterer)

    }}

}
