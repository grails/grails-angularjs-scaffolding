package grails.angular.scaffolding

import grails.codegen.model.Model
import grails.codegen.model.ModelBuilder
import grails.core.GrailsApplication
import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.command.GrailsApplicationCommand
import grails.plugin.angular.scaffolding.element.AngularMarkupBuilder
import grails.plugin.angular.scaffolding.element.PropertyType
import grails.plugin.angular.scaffolding.model.AngularModel
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.renderers.AngularModuleEditor
import grails.plugin.angular.scaffolding.renderers.AngularPropertyRenderer
import grails.plugin.formfields.BeanPropertyAccessor
import grails.plugin.formfields.BeanPropertyAccessorFactory
import groovy.json.JsonOutput
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.validation.GrailsDomainClassValidator
import org.springframework.beans.factory.annotation.Value

class NgGenerateAllCommand implements GrailsApplicationCommand {

    MappingContext grailsDomainClassMappingContext
    BeanPropertyAccessorFactory beanPropertyAccessorFactory
    DomainModelService domainModelService
    AngularPropertyRenderer angularPropertyRenderer
    AngularModuleEditor angularModuleEditor
    AngularMarkupBuilder angularMarkupBuilder

    private PersistentEntity domainClass
    private GrailsDomainClass grailsDomainClass

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
            grailsDomainClass = ((GrailsDomainClassValidator) grailsDomainClassMappingContext.getEntityValidator(domainClass)).domainClass
            Object bean = domainClass.newInstance()

            List<String> formFields = []
            Map<GrailsDomainClassProperty, String> listProperties = [:]

            List<BeanPropertyAccessor> associatedProperties = []

            for (property in domainModelService.getEditableProperties(grailsDomainClass)) {
                BeanPropertyAccessor propertyAccessor = beanPropertyAccessorFactory.accessorFor(bean, property.name)
                if (property?.embedded) {
                    formFields.add(angularPropertyRenderer.renderEditEmbedded(bean, propertyAccessor))
                } else {
                    formFields.add(angularPropertyRenderer.renderEdit(propertyAccessor))
                }
                if (domainModelService.getPropertyType(propertyAccessor) == PropertyType.ASSOCIATION) {
                    associatedProperties.add(propertyAccessor)
                }
            }

            String showForm = angularPropertyRenderer.renderDisplay(bean, grailsDomainClass)

            domainModelService.getShortListVisibleProperties(grailsDomainClass).each {
                BeanPropertyAccessor propertyAccessor = beanPropertyAccessorFactory.accessorFor(bean, it.name)
                listProperties[it] = angularPropertyRenderer.renderPropertyDisplay(propertyAccessor, false)
            }

            AngularModel module = model(domainClass.javaClass)

            AngularModel supportingModule = module

            Map dependencies = ['"ui.router"': '/angular/angular-ui-router']

            AngularModel coreModule = model("${module.packageName}.Core")

            if (coreModule.exists()) {
                dependencies["\"${coreModule.moduleName}\""] = "/${coreModule.modulePath}/${coreModule.moduleName}"
                supportingModule = coreModule
            }

            Boolean hasFileProperty = domainModelService.hasPropertyType(grailsDomainClass, PropertyType.FILE)
            Boolean hasTimeZoneProperty = domainModelService.hasPropertyType(grailsDomainClass, PropertyType.TIMEZONE)
            Boolean hasCurrencyProperty = domainModelService.hasPropertyType(grailsDomainClass, PropertyType.CURRENCY)
            Boolean hasLocaleProperty = domainModelService.hasPropertyType(grailsDomainClass, PropertyType.LOCALE)

            AngularModel parentModule = module.parentModule
            if (parentModule?.exists()) {
                if (angularModuleEditor.addDependency(parentModule.file, module)) {
                    addStatus("Added ${module.moduleName} as a dependency to ${parentModule.moduleName}")
                }
            }

            String controllerName = angularMarkupBuilder.controllerName

            render template: template('angular/javascripts/module.js'),
                    destination: module.file,
                    model: module.asMap() << [dependencies: dependencies, controllerAs: controllerName],
                    overwrite: true

            Map createEditInjections = [:]
            Map domainInjections = [:]

            associatedProperties.each { BeanPropertyAccessor property ->
                AngularModel associatedModule = handleAssociatedProperty(property)
                angularModuleEditor.addDependency(module.file, associatedModule)
                createEditInjections[associatedModule.className] = "${controllerName}.${associatedModule.propertyName}List = ${associatedModule.className}.list();"
                domainInjections[associatedModule.className] = associatedModule.propertyName
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

            render template: template('angular/javascripts/controllers/createController.js'),
                   destination: file("${basePath}/${modulePath}/controllers/${module.propertyName}CreateController.js"),
                   model: artefactParams << [injections: createEditInjections],
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

            render template: template("angular/javascripts/${hasFileProperty ? "multipartDomain" : "domain"}.js"),
                   destination: file("${basePath}/${modulePath}/domain/${module.className}.js"),
                   model: artefactParams << [injections: domainInjections],
                   overwrite: true



        } catch (e) {
            println e.message
            println e.cause
            println e.stackTrace
        }
        return true
    }

    AngularModel handleAssociatedProperty(BeanPropertyAccessor property) {
        AngularModel module = model(property.propertyType)

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
                    overwrite: true

            render template: template("angular/javascripts/${domainModelService.hasPropertyType(property.beanClass, PropertyType.FILE) ? "multipartDomain" : "domain"}.js"),
                    destination: file("${basePath}/${modulePath}/domain/${module.className}.js"),
                    model: module.asMap() << [controllerAs: controllerName, injections: [:]],
                    overwrite: true
       // }

        module
    }
}
