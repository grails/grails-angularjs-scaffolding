package grails.plugin.scaffolding.angular.registry

import grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRenderer
import grails.plugin.scaffolding.angular.registry.input.AngularAssociationInputRenderer
import grails.plugin.scaffolding.angular.registry.input.AngularBidirectionalToManyInputRenderer
import grails.plugin.scaffolding.angular.registry.input.AngularCurrencyInputRenderer
import grails.plugin.scaffolding.angular.registry.input.AngularFileInputRenderer
import grails.plugin.scaffolding.angular.registry.input.AngularTimeZoneInputRenderer
import grails.plugin.scaffolding.angular.registry.output.AngularDateOutputRenderer
import grails.plugin.scaffolding.angular.registry.output.AngularDefaultOutputRenderer
import grails.plugin.scaffolding.angular.registry.output.AngularIdOutputRenderer
import grails.plugin.scaffolding.angular.registry.output.AngularToManyOutputRenderer
import grails.plugin.scaffolding.angular.registry.output.AngularToOneOutputRenderer
import grails.plugin.scaffolding.registry.DomainInputRendererRegistry
import grails.plugin.scaffolding.registry.DomainOutputRendererRegistry
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct
import javax.annotation.Resource


class AngularDomainRendererRegisterer {

    @Resource
    AngularPropertyMarkupRenderer propertyMarkupRenderer

    @Autowired
    DomainInputRendererRegistry domainInputRendererRegistry

    @Autowired
    DomainOutputRendererRegistry domainOutputRendererRegistry

    @PostConstruct
    void registerRenderers() {
        final String controllerName = propertyMarkupRenderer.controllerName

        domainInputRendererRegistry.registerDomainRenderer(new AngularTimeZoneInputRenderer(), 1)
        domainInputRendererRegistry.registerDomainRenderer(new AngularFileInputRenderer(), 1)
        domainInputRendererRegistry.registerDomainRenderer(new AngularCurrencyInputRenderer(), 1)
        domainInputRendererRegistry.registerDomainRenderer(new AngularBidirectionalToManyInputRenderer(controllerName), 1)
        domainInputRendererRegistry.registerDomainRenderer(new AngularAssociationInputRenderer(controllerName), 0)

        domainOutputRendererRegistry.registerDomainRenderer(new AngularDateOutputRenderer(controllerName), 1)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularDefaultOutputRenderer(controllerName), 0)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularIdOutputRenderer(), 1)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularToManyOutputRenderer(controllerName), 1)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularToOneOutputRenderer(controllerName), 1)
    }
}
