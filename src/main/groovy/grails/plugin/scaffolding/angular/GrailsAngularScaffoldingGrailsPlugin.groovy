package grails.plugin.scaffolding.angular

import grails.plugin.scaffolding.angular.json.AngularJsonMarshaller
import grails.plugin.scaffolding.angular.markup.AngularContextMarkupRendererImpl
import grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRendererImpl
import grails.plugin.scaffolding.angular.registry.AngularDomainRendererRegisterer
import grails.plugin.scaffolding.model.DomainModelServiceImpl
import grails.plugin.scaffolding.model.property.DomainPropertyFactoryImpl
import grails.plugin.scaffolding.angular.template.AngularModuleEditorImpl
import grails.plugin.scaffolding.markup.DomainMarkupRendererImpl
import grails.plugin.scaffolding.registry.DomainInputRendererRegistry
import grails.plugin.scaffolding.registry.DomainOutputRendererRegistry
import grails.plugin.scaffolding.registry.DomainRendererRegisterer
import grails.plugins.*

class GrailsAngularScaffoldingGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.2.0.BUILD-SNAPSHOT > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Grails Angular Scaffolding" // Headline output name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-angular-scaffolding"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() { {->
        domainModelService(DomainModelServiceImpl)

        domainOutputRendererRegistry(DomainOutputRendererRegistry)

        domainInputRendererRegistry(DomainInputRendererRegistry)

        domainPropertyFactory(DomainPropertyFactoryImpl)

        domainMarkupRenderer(DomainMarkupRendererImpl)

        domainRendererRegisterer(DomainRendererRegisterer)

        propertyMarkupRenderer(AngularPropertyMarkupRendererImpl)

        contextMarkupRenderer(AngularContextMarkupRendererImpl)

        angularModuleEditor(AngularModuleEditorImpl)

        angularJsonMarshaller(AngularJsonMarshaller)

        angulardDomainRendererRegisterer(AngularDomainRendererRegisterer)

    }}

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
