package org.grails.plugin.scaffolding.angular.registry

import org.grails.plugin.scaffolding.angular.registry.input.AngularAssociationInputRenderer
import org.grails.plugin.scaffolding.angular.registry.input.AngularCurrencyInputRenderer
import org.grails.plugin.scaffolding.angular.registry.input.AngularFileInputRenderer
import org.grails.plugin.scaffolding.angular.registry.input.AngularTimeZoneInputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularToManyOutputRenderer
import org.grails.plugin.scaffolding.angular.markup.AngularPropertyMarkupRenderer
import org.grails.plugin.scaffolding.angular.registry.input.AngularBidirectionalToManyInputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularDateOutputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularDefaultOutputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularIdOutputRenderer
import org.grails.plugin.scaffolding.angular.registry.output.AngularToOneOutputRenderer
import org.grails.scaffolding.registry.DomainInputRendererRegistry
import org.grails.scaffolding.registry.DomainOutputRendererRegistry
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

        domainInputRendererRegistry.registerDomainRenderer(new AngularAssociationInputRenderer(controllerName), 0)
        domainInputRendererRegistry.registerDomainRenderer(new AngularTimeZoneInputRenderer(), 0)
        domainInputRendererRegistry.registerDomainRenderer(new AngularFileInputRenderer(), 0)
        domainInputRendererRegistry.registerDomainRenderer(new AngularCurrencyInputRenderer(), 0)
        domainInputRendererRegistry.registerDomainRenderer(new AngularBidirectionalToManyInputRenderer(controllerName), 0)

        domainOutputRendererRegistry.registerDomainRenderer(new AngularDefaultOutputRenderer(controllerName), 0)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularDateOutputRenderer(controllerName), 0)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularIdOutputRenderer(controllerName), 0)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularToManyOutputRenderer(controllerName), 0)
        domainOutputRendererRegistry.registerDomainRenderer(new AngularToOneOutputRenderer(controllerName), 0)

    }
}
