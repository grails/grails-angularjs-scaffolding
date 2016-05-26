package grails.plugin.scaffolding.registry

import grails.plugin.scaffolding.registry.input.DefaultDomainInputRenderer

class DomainInputRendererRegistry extends DomainRendererRegistry<DomainInputRenderer> {

    DomainInputRenderer getDefaultRenderer() {
        new DefaultDomainInputRenderer()
    }

}