package grails.angular.scaffolding

import grails.plugin.angular.scaffolding.command.GrailsApplicationCommand
import grails.plugin.angular.scaffolding.element.AngularMarkupBuilder
import grails.plugin.angular.scaffolding.model.property.PropertyType
import grails.plugin.angular.scaffolding.model.AngularModel
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.model.property.DomainProperty
import grails.plugin.angular.scaffolding.renderers.AngularModuleEditor
import grails.plugin.angular.scaffolding.renderers.AngularPropertyRenderer
import grails.plugin.angular.scaffolding.templates.AngularDomainHelper
import grails.plugin.angular.scaffolding.templates.CreateControllerHelper
import groovy.json.JsonOutput
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.types.Embedded
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.ToMany
import org.grails.datastore.mapping.model.types.ToOne
import org.springframework.beans.factory.annotation.Value

class NgGenerateAllCommand implements GrailsApplicationCommand {

    MappingContext grailsDomainClassMappingContext
    DomainModelService domainModelService
    AngularPropertyRenderer angularPropertyRenderer
    AngularModuleEditor angularModuleEditor
    AngularMarkupBuilder angularMarkupBuilder

    private PersistentEntity domainClass

    String assetPath

    @Value('${grails.codegen.angular.assetDir:javascripts}')
    String setAssetPath(String assetPath) {
        this.assetPath = assetPath
        this.basePath = "grails-app/assets/${assetPath}"
    }

    @Override
    boolean handle() {
        try {
            domainClass = grailsDomainClassMappingContext.getPersistentEntity(args[0])

            List<String> formFields = []
            Map<DomainProperty, String> listProperties = [:]

            List<DomainProperty> associatedProperties = []

            for (property in domainModelService.getEditableProperties(domainClass)) {
                if (property.persistentProperty instanceof Embedded) {
                    formFields.add(angularPropertyRenderer.renderEditEmbedded(property))
                } else {
                    formFields.add(angularPropertyRenderer.renderEdit(property))
                }
                if ([PropertyType.ASSOCIATION, PropertyType.ONETOMANY].contains(domainModelService.getPropertyType(property))) {
                    associatedProperties.add(property)
                }
            }

            String showForm = angularPropertyRenderer.renderDisplay(domainClass)

            domainModelService.getShortListVisibleProperties(domainClass).each { DomainProperty property ->
                listProperties[property] = angularPropertyRenderer.renderPropertyDisplay(property, false)
            }

            AngularModel module = model(domainClass.javaClass)

            AngularModel supportingModule = module

            Map dependencies = ['"ui.router"': '/angular/angular-ui-router']

            AngularModel coreModule = model("${module.packageName}.Core")

            if (coreModule.exists()) {
                dependencies["\"${coreModule.moduleName}\""] = "/${coreModule.modulePath}/${coreModule.moduleName}"
                supportingModule = coreModule
            }

            Boolean hasFileProperty = domainModelService.hasPropertyType(domainClass, PropertyType.FILE)
            Boolean hasTimeZoneProperty = domainModelService.hasPropertyType(domainClass, PropertyType.TIMEZONE)
            Boolean hasCurrencyProperty = domainModelService.hasPropertyType(domainClass, PropertyType.CURRENCY)
            Boolean hasLocaleProperty = domainModelService.hasPropertyType(domainClass, PropertyType.LOCALE)

            AngularModel parentModule = module.parentModule
            if (parentModule?.exists()) {
                if (angularModuleEditor.addDependency(parentModule.file, module)) {
                    addStatus("Added ${module.moduleName} as a dependency to ${parentModule.moduleName}")
                }
            }

            String controllerName = angularMarkupBuilder.controllerName

            CreateControllerHelper createControllerHelper = new CreateControllerHelper(associatedProperties)

            render template: template('angular/javascripts/module.js'),
                    destination: module.file,
                    model: module.asMap() << [dependencies: dependencies, controllerAs: controllerName, createParams: createControllerHelper.stateParams],
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

            render template: template('angular/views/form.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/form.tpl.html"),
                    model: [fields: formFields],
                    overwrite: true

            render template: template('angular/views/show.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/show.tpl.html"),
                    model: module.asMap() << [showForm: showForm, controllerName: controllerName],
                    overwrite: true

            render template: template('angular/views/list.tpl.html'),
                    destination: file("${basePath}/${modulePath}/templates/list.tpl.html"),
                    model: module.asMap() << [listProperties: listProperties, controllerName: controllerName],
                    overwrite: true


            Map artefactParams = module.asMap() << [controllerAs: controllerName]

            if (hasFileProperty) {
                render template: template("angular/javascripts/directives/fileModel.js"),
                        destination: file("${basePath}/${supportingModule.modulePath}/directives/fileModel.js"),
                        model: [moduleName: supportingModule.moduleName],
                        overwrite: true
            }

            if (hasTimeZoneProperty) {
                createEditInjections["timeZoneService"] = "${controllerName}.timeZoneList = timeZoneService.get();"
                render template: template("angular/javascripts/services/timeZoneService.js"),
                        destination: file("${basePath}/${supportingModule.modulePath}/services/timeZoneService.js"),
                        model: [moduleName: supportingModule.moduleName, timeZones: JsonOutput.prettyPrint(JsonOutput.toJson(domainModelService.timeZones))],
                        overwrite: true
            }

            if (hasCurrencyProperty) {
                createEditInjections["currencyService"] = "${controllerName}.currencyList = currencyService.get();"
                render template: template("angular/javascripts/services/currencyService.js"),
                        destination: file("${basePath}/${supportingModule.modulePath}/services/currencyService.js"),
                        model: [moduleName: supportingModule.moduleName, currencies: JsonOutput.prettyPrint(JsonOutput.toJson(domainModelService.currencyCodes))],
                        overwrite: true
            }

            if (hasLocaleProperty) {
                createEditInjections["localeService"] = "${controllerName}.localeList = localeService.get();"
                render template: template("angular/javascripts/services/localeService.js"),
                        destination: file("${basePath}/${supportingModule.modulePath}/services/localeService.js"),
                        model: [moduleName: supportingModule.moduleName, locales: JsonOutput.prettyPrint(JsonOutput.toJson(domainModelService.locales))],
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

            AngularDomainHelper angularModuleHelper = new AngularDomainHelper(associatedProperties)

            render template: template("angular/javascripts/${hasFileProperty ? "multipartDomain" : "domain"}.js"),
                   destination: file("${basePath}/${modulePath}/domain/${module.className}.js"),
                   model: artefactParams << [injections: angularModuleHelper.moduleConfig, getConfig: angularModuleHelper.getConfig, queryConfig: angularModuleHelper.queryConfig],
                   overwrite: true



        } catch (e) {
            println e.message
            println e.cause
            println e.stackTrace
        }
        return true
    }

    AngularModel handleAssociatedProperty(DomainProperty property) {
        AngularModel module = model(property.associatedType)

        //if (!module.exists()) {
            final String controllerName = angularMarkupBuilder.controllerName

            AngularModel parentModule = module.parentModule
            if (parentModule?.exists()) {
                if (angularModuleEditor.addDependency(parentModule.file, module)) {
                    addStatus("Added ${module.moduleName} as a dependency to ${parentModule.moduleName}")
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
                    model: module.asMap() << [dependencies: dependencies, controllerAs: controllerName],
                    overwrite: false

            render template: template("angular/javascripts/${domainModelService.hasPropertyType(property.owner, PropertyType.FILE) ? "multipartDomain" : "domain"}.js"),
                    destination: file("${basePath}/${modulePath}/domain/${module.className}.js"),
                    model: module.asMap() << [controllerAs: controllerName, injections: '', getConfig: '', queryConfig: ''],
                    overwrite: false
       // }

        module
    }
}
