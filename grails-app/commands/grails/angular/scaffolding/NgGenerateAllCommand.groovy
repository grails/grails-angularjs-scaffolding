package grails.angular.scaffolding

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

    String assetPath

    @Value('${grails.codegen.angular.assetDir:javascripts}')
    String setAssetPath(String assetPath) {
        this.assetPath = assetPath
        this.basePath = "grails-app/assets/${assetPath}"
    }

    @Value('${grails.codegen.angular.uiRouterPath:/angular/angular-ui-router}')
    String uiRouterPath

    @Value('${grails.codegen.angular.angularPath:/angular/angular}')
    String angularPath

    @Override
    boolean handle() {

        String domainClassName = args[0]

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
            property.persistentProperty instanceof Association
        }

        AngularModel module = model(domainClass.javaClass)

        AngularModel supportingModule = module

        Map dependencies = ['"ui.router"': uiRouterPath]

        AngularModel coreModule = model("${module.packageName}.Core")

        if (coreModule.exists()) {
            dependencies["\"${coreModule.moduleName}\""] = "/${coreModule.modulePath}/${coreModule.moduleName}"
            supportingModule = coreModule
        }

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
                overwrite: true

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
                overwrite: true

        render template: template('angular/views/edit.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/edit.tpl.html"),
                model: module.asMap() << [controllerName: controllerName],
                overwrite: true

        render(formTemplate, file("${basePath}/${modulePath}/templates/form.tpl.html"), [:], true)

        render template: template('angular/views/show.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/show.tpl.html"),
                model: module.asMap() << [showForm: showTemplate, controllerName: controllerName],
                overwrite: true

        render template: template('angular/views/list.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/list.tpl.html"),
                model: module.asMap() << [listTemplate: listTemplate, controllerName: controllerName],
                overwrite: true


        Map artefactParams = module.asMap() << [controllerAs: controllerName]

        if (hasFileProperty) {
            render template: template("angular/javascripts/directives/fileModel.js"),
                    destination: file("${basePath}/${supportingModule.modulePath}/directives/fileModel.js"),
                    model: [moduleName: supportingModule.moduleName],
                    overwrite: true
        }

        if (associatedProperties) {
            if (associatedProperties.any { it.persistentProperty instanceof ToMany}) {
                render template: template("angular/javascripts/services/domainToManyConversion.js"),
                        destination: file("${basePath}/${supportingModule.modulePath}/services/domainToManyConversion.js"),
                        model: [moduleName: supportingModule.moduleName],
                        overwrite: true
            }

            render template: template("angular/javascripts/services/domainListConversion.js"),
                    destination: file("${basePath}/${supportingModule.modulePath}/services/domainListConversion.js"),
                    model: [moduleName: supportingModule.moduleName],
                    overwrite: true

            render template: template("angular/javascripts/services/domainConversion.js"),
                    destination: file("${basePath}/${supportingModule.modulePath}/services/domainConversion.js"),
                    model: [moduleName: supportingModule.moduleName],
                    overwrite: true
        }

        render template: template('angular/javascripts/controllers/createController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}CreateController.js"),
               model: artefactParams << [injections: createEditInjections, createParams: createControllerHelper.controllerStatements],
               overwrite: true

        render template: template('angular/javascripts/controllers/editController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}EditController.js"),
               model: artefactParams << [injections: createEditInjections],
               overwrite: true

        render template: template('angular/javascripts/controllers/listController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}ListController.js"),
               model: artefactParams,
               overwrite: true

        render template: template('angular/javascripts/controllers/showController.js'),
               destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}ShowController.js"),
               model: artefactParams,
               overwrite: true

        AngularDomainHelper angularDomainHelper = new AngularDomainHelper(domainClass, associatedProperties, grailsUrlMappingsHolder)

        render template: template("angular/javascripts/${hasFileProperty ? "multipartDomain" : "domain"}.js"),
               destination: file("${basePath}/${modulePath}/domain/${module.className}.js"),
               model: artefactParams << [injections: angularDomainHelper.moduleConfig, getConfig: angularDomainHelper.getConfig, queryConfig: angularDomainHelper.queryConfig, uri: angularDomainHelper.uri],
               overwrite: true


        true
    }

    AngularModel handleAssociatedProperty(DomainProperty property) {
        AngularModel module = model(property.associatedType)

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
        Boolean hasFileProperty = domainModelService.hasInputProperty(property.associatedEntity) { DomainProperty domainProperty ->
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
