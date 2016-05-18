package grails.angular.scaffolding

import grails.codegen.model.ModelBuilder
import grails.core.GrailsApplication
import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.command.GrailsApplicationCommand
import grails.plugin.angular.scaffolding.element.AngularMarkupBuilder
import grails.plugin.angular.scaffolding.element.PropertyType
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.renderers.AngularModuleEditor
import grails.plugin.angular.scaffolding.renderers.AngularPropertyRenderer
import grails.plugin.formfields.BeanPropertyAccessor
import grails.plugin.formfields.BeanPropertyAccessorFactory
import groovy.json.JsonOutput
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.validation.GrailsDomainClassValidator

class NgGenerateAllCommand implements GrailsApplicationCommand, ModelBuilder {

    MappingContext grailsDomainClassMappingContext
    BeanPropertyAccessorFactory beanPropertyAccessorFactory
    DomainModelService domainModelService
    AngularPropertyRenderer angularPropertyRenderer
    GrailsApplication grailsApplication
    AngularModuleEditor angularModuleEditor
    AngularMarkupBuilder angularMarkupBuilder

    private PersistentEntity domainClass
    private GrailsDomainClass grailsDomainClass

    @Override
    boolean handle() {
        try {
            domainClass = grailsDomainClassMappingContext.getPersistentEntity(args[0])
            grailsDomainClass = ((GrailsDomainClassValidator) grailsDomainClassMappingContext.getEntityValidator(domainClass)).domainClass
            Object bean = domainClass.newInstance()

            List<String> formFields = []
            Map<GrailsDomainClassProperty, String> listProperties = [:]

            for (property in domainModelService.getEditableProperties(grailsDomainClass)) {
                BeanPropertyAccessor propertyAccessor = beanPropertyAccessorFactory.accessorFor(bean, property.name)
                if (property?.embedded) {
                    formFields.add(angularPropertyRenderer.renderEditEmbedded(bean, propertyAccessor))
                } else {
                    formFields.add(angularPropertyRenderer.renderEdit(propertyAccessor))
                }
            }

            String showForm = angularPropertyRenderer.renderDisplay(bean, grailsDomainClass)

            domainModelService.getVisibleProperties(grailsDomainClass).each {
                BeanPropertyAccessor propertyAccessor = beanPropertyAccessorFactory.accessorFor(bean, it.name)
                listProperties[it] = angularMarkupBuilder.renderPropertyDisplay(propertyAccessor, false)
            }

            def model = model(domainClass.javaClass)

            final String assetPath = grailsApplication.config.getProperty("grails.codegen.angular.assetDir", String) ?: "javascripts"
            final String basePath = "grails-app/assets/${assetPath}"
            final String modulePath = "${model.packagePath}/${model.propertyName}"
            final String moduleName = "${model.packageName}.${model.propertyName}"
            final String jsModulePath = modulePath.replaceAll('\\\\', '/');

            Map dependencies = [:]

            if (file("${basePath}/${model.packagePath}/core/${model.packageName}.core.js").exists()) {
                String coreModuleName = "\"${model.packageName}.core\""
                String coreAssetPath = "/${model.packagePath.replaceAll('\\\\', '/')}/core/${model.packageName}.core"
                dependencies[coreModuleName] = coreAssetPath
            }
            Boolean hasFileProperty = domainModelService.hasFileProperty(grailsDomainClass)
            Boolean hasTimeZoneProperty = domainModelService.hasTimeZoneProperty(grailsDomainClass)

            if (hasFileProperty) {
                render template: template("angular/javascripts/directives/fileModel.js"),
                        destination: file("${basePath}/${modulePath}/directives/fileModel.js"),
                        model: [moduleName: moduleName],
                        overwrite: true
            }

            if (hasTimeZoneProperty) {
                render template: template("angular/javascripts/services/timeZoneService.js"),
                        destination: file("${basePath}/${modulePath}/services/timeZoneService.js"),
                        model: [moduleName: moduleName, timeZones: JsonOutput.prettyPrint(JsonOutput.toJson(domainModelService.timeZones))],
                        overwrite: true
            }


            dependencies['"ui.router"'] = '/angular/angular-ui-router'

            File parentModule = file("${basePath}/${model.packagePath}/${model.packageName}.js")
            if (parentModule.exists()) {
                if (angularModuleEditor.addDependency(parentModule, model)) {
                    addStatus("Added ${moduleName} as a dependency to ${parentModule.name}")
                }
            }

            String controllerName = angularMarkupBuilder.controllerName



            render template: template('angular/javascripts/module.js'),
                    destination: file("${basePath}/${modulePath}/${moduleName}.js"),
                    model: model.asMap() << [fullName: moduleName, dependencies: dependencies, modulePath: jsModulePath, controllerAs: controllerName],
                    overwrite: true

            render template: template('angular/views/create.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/create.tpl.html"),
                    model: model.asMap() << [modulePath: jsModulePath, controllerName: controllerName],
                    overwrite: true

            render template: template('angular/views/edit.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/edit.tpl.html"),
                    model: model.asMap() << [modulePath: jsModulePath, controllerName: controllerName],
                    overwrite: true

            render template: template('angular/views/form.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/form.tpl.html"),
                    model: [fields: formFields],
                    overwrite: true

            render template: template('angular/views/show.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/show.tpl.html"),
                    model: model.asMap() << [showForm: showForm, controllerName: controllerName],
                    overwrite: true

            render template: template('angular/views/list.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/list.tpl.html"),
                    model: model.asMap() << [listProperties: listProperties, controllerName: controllerName],
                    overwrite: true


            Map artefactParams = model.asMap() << [moduleName: moduleName, controllerAs: controllerName, injectTimeZone: hasTimeZoneProperty]

            render template: template('angular/javascripts/controllers/createController.js'),
                   destination: file("${basePath}/${modulePath}/controllers/${model.propertyName}CreateController.js"),
                   model: artefactParams,
                   overwrite: true

            render template: template('angular/javascripts/controllers/editController.js'),
                   destination: file("${basePath}/${modulePath}/controllers/${model.propertyName}EditController.js"),
                   model: artefactParams,
                   overwrite: true

            render template: template('angular/javascripts/controllers/listController.js'),
                   destination: file("${basePath}/${modulePath}/controllers/${model.propertyName}ListController.js"),
                   model: artefactParams,
                   overwrite: true

            render template: template('angular/javascripts/controllers/showController.js'),
                   destination: file("${basePath}/${modulePath}/controllers/${model.propertyName}ShowController.js"),
                   model: artefactParams,
                   overwrite: true

            render template: template("angular/javascripts/${hasFileProperty ? "multipartDomain" : "domain"}.js"),
                   destination: file("${basePath}/${modulePath}/domain/${model.className}.js"),
                   model: artefactParams,
                   overwrite: true

            if (hasFileProperty) {
                render template: template("angular/javascripts/directives/fileModel.js"),
                       destination: file("${basePath}/${modulePath}/directives/fileModel.js"),
                       model: [moduleName: moduleName],
                       overwrite: true
            }

            if (hasTimeZoneProperty) {
                render template: template("angular/javascripts/services/timeZoneService.js"),
                       destination: file("${basePath}/${modulePath}/services/timeZoneService.js"),
                       model: [moduleName: moduleName, timeZones: JsonOutput.prettyPrint(JsonOutput.toJson(domainModelService.timeZones))],
                       overwrite: true
            }

        } catch (e) {
            println e.message
            println e.cause
            println e.stackTrace
        }
        return true
    }
}
