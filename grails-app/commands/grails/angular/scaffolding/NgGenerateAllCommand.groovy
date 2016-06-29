package grails.angular.scaffolding

import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRenderer
import org.grails.plugin.scaffolding.command.GrailsApplicationCommand
import org.grails.plugin.scaffolding.angular.model.AngularModel
import org.grails.scaffolding.markup.DomainMarkupRenderer
import org.grails.scaffolding.model.DomainModelService
import org.grails.scaffolding.model.property.DomainProperty
import org.grails.plugin.scaffolding.angular.template.AngularModuleEditor
import org.grails.plugin.scaffolding.angular.template.AngularDomainHelper
import org.grails.plugin.scaffolding.angular.template.CreateControllerHelper
import grails.web.mapping.UrlMappings
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ToMany
import org.grails.scaffolding.registry.input.FileInputRenderer
import org.springframework.beans.factory.annotation.Value

class NgGenerateAllCommand implements GrailsApplicationCommand {

    MappingContext grailsDomainClassMappingContext
    DomainModelService domainModelService
    DomainMarkupRenderer domainMarkupRenderer
    AngularModuleEditor angularModuleEditor
    UrlMappings grailsUrlMappingsHolder
    AngularPropertyMarkupRenderer propertyMarkupRenderer

    private PersistentEntity domainClass

    @Value('${grails.codegen.angular.baseDir:grails-app/assets}')
    String baseDir

    @Value('${grails.codegen.angular.assetDir:javascripts}')
    String assetPath

    @Value('${grails.codegen.angular.uiRouterPath:/angular/angular-ui-router}')
    String uiRouterPath

    @Value('${grails.codegen.angular.angularPath:/angular/angular}')
    String angularPath

    @Value('${grails.codegen.angular.ngResourcePath:/angular/angular-resource}')
    String ngResourcePath

    @Override
    boolean handle() {
        this.basePath = "$baseDir/$assetPath"

        String domainClassName = args[0]

        boolean overwrite = (args[1] instanceof String && args[1].toLowerCase() == "true")

        try {
            domainClass = grailsDomainClassMappingContext.getPersistentEntity(domainClassName)
        } catch (e) {
            System.err.println("Error | The domain class you entered: \"${domainClassName}\" could not be found")
            return
        }

        String formTemplate = domainMarkupRenderer.renderInput(domainClass)
        String showTemplate = domainMarkupRenderer.renderOutput(domainClass)
        String listTemplate = domainMarkupRenderer.renderListOutput(domainClass)

        List<DomainProperty> associatedProperties = domainModelService.findInputProperties(domainClass) { DomainProperty property ->
            PersistentProperty prop = property.persistentProperty
            prop instanceof Association
        }

        AngularModel module = model(domainClass.javaClass)

        Map dependencies = ['"ui.router"': uiRouterPath, '"ngResource"': ngResourcePath]

        AngularModel coreModule = model("${module.packageName}.Core")

        render template: template('angular/javascripts/coreModule.js'),
                destination: coreModule.file,
                model: coreModule.asMap() << [angularPath: angularPath],
                overwrite: false

        dependencies["\"${coreModule.moduleName}\""] = "/${coreModule.modulePath}/${coreModule.moduleName}"

        FileInputRenderer fileInputRenderer = new FileInputRenderer()
        Boolean hasFileProperty = domainModelService.hasInputProperty(domainClass) { DomainProperty property ->
            fileInputRenderer.supports(property)
        }

        AngularModel parentModule = module.parentModule
        if (parentModule?.exists()) {
            if (angularModuleEditor.addDependency(parentModule.file, module)) {
                println("Added ${module.moduleName} as a dependency to ${parentModule.moduleName}")
            }
        }

        String controllerName = propertyMarkupRenderer.controllerName

        CreateControllerHelper createControllerHelper = new CreateControllerHelper(associatedProperties)

        render template: template('angular/javascripts/module.js'),
                destination: module.file,
                model: module.asMap() << [angularPath: angularPath, dependencies: dependencies, controllerAs: controllerName, createParams: createControllerHelper.stateParams],
                overwrite: overwrite

        Map createEditInjections = [:]

        associatedProperties.each { DomainProperty property ->
            AngularModel associatedModule = handleAssociatedProperty(property)
            angularModuleEditor.addDependency(module.file, associatedModule)
            createEditInjections[associatedModule.className] = "${controllerName}.${associatedModule.propertyName}List = ${associatedModule.className}.list();"
        }

        final String modulePath = module.modulePath

        render template: template('angular/views/create.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/create.tpl.html"),
                model: module.asMap() << [controllerName: controllerName],
                overwrite: overwrite

        render template: template('angular/views/edit.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/edit.tpl.html"),
                model: module.asMap() << [controllerName: controllerName],
                overwrite: overwrite

        render(formTemplate, file("${basePath}/${modulePath}/templates/form.tpl.html"), [:], true)

        render template: template('angular/views/show.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/show.tpl.html"),
                model: module.asMap() << [showForm: showTemplate, controllerName: controllerName],
                overwrite: overwrite

        render template: template('angular/views/list.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/list.tpl.html"),
                model: module.asMap() << [listTemplate: listTemplate, controllerName: controllerName],
                overwrite: overwrite


        Map artefactParams = module.asMap() << [controllerAs: controllerName]

        if (hasFileProperty) {
            render template: template("angular/javascripts/directives/fileModel.js"),
                    destination: file("${basePath}/${coreModule.modulePath}/directives/fileModel.js"),
                    model: [moduleName: coreModule.moduleName],
                    overwrite: overwrite
        }

        if (associatedProperties) {
            if (associatedProperties.any { it.persistentProperty instanceof ToMany}) {
                render template: template("angular/javascripts/services/domainToManyConversion.js"),
                        destination: file("${basePath}/${coreModule.modulePath}/services/domainToManyConversion.js"),
                        model: [moduleName: coreModule.moduleName],
                        overwrite: overwrite
            }

            render template: template("angular/javascripts/services/domainListConversion.js"),
                    destination: file("${basePath}/${coreModule.modulePath}/services/domainListConversion.js"),
                    model: [moduleName: coreModule.moduleName],
                    overwrite: overwrite

            render template: template("angular/javascripts/services/domainConversion.js"),
                    destination: file("${basePath}/${coreModule.modulePath}/services/domainConversion.js"),
                    model: [moduleName: coreModule.moduleName],
                    overwrite: overwrite
        }

        render template: template('angular/javascripts/controllers/createController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}CreateController.js"),
               model: artefactParams << [injections: createEditInjections, createParams: createControllerHelper.controllerStatements],
               overwrite: overwrite

        render template: template('angular/javascripts/controllers/editController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}EditController.js"),
               model: artefactParams << [injections: createEditInjections],
               overwrite: overwrite

        render template: template('angular/javascripts/controllers/listController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}ListController.js"),
               model: artefactParams,
               overwrite: overwrite

        render template: template('angular/javascripts/controllers/showController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}ShowController.js"),
               model: artefactParams,
               overwrite: overwrite



        AngularDomainHelper angularDomainHelper = new AngularDomainHelper(domainClass, associatedProperties, grailsUrlMappingsHolder)

        render template: template("angular/javascripts/${hasFileProperty ? "multipartDomain" : "domain"}.js"),
               destination: file("${basePath}/${modulePath}/domain/${module.className}.js"),
               model: artefactParams << [injections: angularDomainHelper.moduleConfig, getConfig: angularDomainHelper.getConfig, queryConfig: angularDomainHelper.queryConfig, uri: angularDomainHelper.uri],
               overwrite: overwrite


        true
    }

    AngularModel handleAssociatedProperty(DomainProperty property) {
        AngularModel module = model(property.associatedType)
        PersistentEntity domainClass = property.associatedEntity
        final String controllerName = propertyMarkupRenderer.controllerName

        AngularModel parentModule = module.parentModule
        if (parentModule?.exists()) {
            if (angularModuleEditor.addDependency(parentModule.file, module)) {
                println("Added ${module.moduleName} as a dependency to ${parentModule.moduleName}")
            }
        }

        Map dependencies = [:]

        AngularModel coreModule = model("${module.packageName}.Core")
        if (coreModule.exists()) {
            dependencies["\"${coreModule.moduleName}\""] = "/${coreModule.modulePath}/${coreModule.moduleName}"
        }

        final String modulePath = module.modulePath

        render template: template('angular/javascripts/associatedModule.js'),
                destination: module.file,
                model: module.asMap() << [angularPath: angularPath, dependencies: dependencies, controllerAs: controllerName],
                overwrite: false

        FileInputRenderer fileInputRenderer = new FileInputRenderer()
        Boolean hasFileProperty = domainModelService.hasInputProperty(domainClass) { DomainProperty domainProperty ->
            fileInputRenderer.supports(domainProperty)
        }

        AngularDomainHelper angularDomainHelper = new AngularDomainHelper(domainClass, [], grailsUrlMappingsHolder)

        render template: template("angular/javascripts/${hasFileProperty ? "multipartDomain" : "domain"}.js"),
                destination: file("${basePath}/${modulePath}/domain/${module.className}.js"),
                model: module.asMap() << [controllerAs: controllerName, injections: '', getConfig: '', queryConfig: '', uri: angularDomainHelper.uri],
                overwrite: false

        module
    }
}
