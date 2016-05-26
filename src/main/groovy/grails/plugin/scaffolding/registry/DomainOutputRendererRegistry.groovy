package grails.plugin.scaffolding.registry

import grails.plugin.scaffolding.registry.output.DefaultDomainOutputRenderer

class DomainOutputRendererRegistry extends DomainRendererRegistry<DomainOutputRenderer> {

    DomainOutputRenderer getDefaultRenderer() {
        new DefaultDomainOutputRenderer()
    }

}
