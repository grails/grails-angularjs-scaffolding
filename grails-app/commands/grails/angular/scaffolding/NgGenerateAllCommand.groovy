package grails.angular.scaffolding

import grails.codegen.model.ModelBuilder
import grails.core.GrailsApplication
import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty
import grails.plugin.angular.scaffolding.command.GrailsApplicationCommand
import grails.plugin.angular.scaffolding.model.DomainModelService
import grails.plugin.angular.scaffolding.renderers.AngularModuleEditor
import grails.plugin.angular.scaffolding.renderers.AngularPropertyRenderer
import grails.plugin.formfields.BeanPropertyAccessorFactory
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

    private PersistentEntity domainClass
    private GrailsDomainClass grailsDomainClass

    @Override
    boolean handle() {
        domainClass = grailsDomainClassMappingContext.getPersistentEntity(args[0])
        grailsDomainClass = ((GrailsDomainClassValidator)grailsDomainClassMappingContext.getEntityValidator(domainClass)).domainClass
        Object bean = domainClass.newInstance()

        List<String> formFields = []
        List<String> showFields = []
        Map<GrailsDomainClassProperty, String> listProperties = [:]
        for (property in domainModelService.getEditableProperties(grailsDomainClass)) {
            def propertyAccessor = beanPropertyAccessorFactory.accessorFor(bean, property.name)
            if (property?.embedded) {
                formFields.add(angularPropertyRenderer.renderEditEmbedded(bean, propertyAccessor))
            } else {
                formFields.add(angularPropertyRenderer.renderEdit(propertyAccessor))
            }
            showFields.add(angularPropertyRenderer.renderDisplay(bean, propertyAccessor))
        }

        domainModelService.getVisibleProperties(grailsDomainClass).each {
            def propertyAccessor = beanPropertyAccessorFactory.accessorFor(bean, it.name)
            listProperties[it] = angularPropertyRenderer.getDisplayWidget(propertyAccessor, null)
        }

        def model = model(domainClass.javaClass)


        final String assetPath = grailsApplication.config.getProperty("grails.codegen.angular.assetDir", String) ?: "javascripts"
        final String basePath = "grails-app/assets/${assetPath}"
        final String modulePath = "${model.packagePath}/${model.propertyName}"
        final String moduleName = "${model.packageName}.${model.propertyName}"
        final String jsModulePath = modulePath.replaceAll('\\\\','/');

        Map dependencies = [:]

        if (file("${basePath}/${model.packagePath}/core/${model.packageName}.core.js").exists()) {
            String coreModuleName = "\"${model.packageName}.core\""
            String coreAssetPath = "/${model.packagePath.replaceAll('\\\\','/')}/core/${model.packageName}.core"
            dependencies[coreModuleName] = coreAssetPath
        }

        dependencies['"ui.router"'] = '/angular/angular-ui-router'

        File parentModule = file("${basePath}/${model.packagePath}/${model.packageName}.js")
        if (parentModule.exists()) {
            angularModuleEditor.addDependency(parentModule, model)
        }

        String controllerName = angularPropertyRenderer.controllerName

        render template: template('angular/javascripts/module.js'),
                destination: file("${basePath}/${modulePath}/${moduleName}.js"),
                model: model.asMap() << [fullName: moduleName, dependencies: dependencies, modulePath: jsModulePath],
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
                model: model.asMap() << [fields: showFields, controllerName: controllerName],
                overwrite: true

        render template: template('angular/views/list.tpl.html'),
                destination: file("${basePath}/${modulePath}/templates/list.tpl.html"),
                model: model.asMap() << [listProperties: listProperties, controllerName: controllerName],
                overwrite: true


        Map artefactParams = model.asMap() << [moduleName: moduleName]

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

        render template: template('angular/javascripts/domain.js'),
                destination: file("${basePath}/${modulePath}/domain/${model.className}.js"),
                model: artefactParams,
                overwrite: true

        return true
    }
}
