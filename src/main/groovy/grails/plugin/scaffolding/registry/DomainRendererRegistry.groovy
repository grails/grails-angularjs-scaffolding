package grails.plugin.scaffolding.registry

import grails.plugin.scaffolding.model.property.DomainProperty
import java.util.concurrent.atomic.AtomicInteger

abstract class DomainRendererRegistry<T extends DomainRenderer> {

    protected SortedSet<Entry> domainRegistryEntries = new TreeSet<Entry>();

    protected final AtomicInteger RENDERER_SEQUENCE = new AtomicInteger(0);

    void registerDomainRenderer(T domainRenderer, Integer priority) {
        domainRegistryEntries.add(new Entry(domainRenderer, priority))
    }

    abstract T getDefaultRenderer()

    T get(DomainProperty domainProperty) {
        for (Entry entry : domainRegistryEntries) {
            if (entry.renderer.supports(domainProperty)) {
                return entry.renderer
            }
        }
        return defaultRenderer
    }

    public class Entry implements Comparable<Entry> {
        protected final T renderer
        private final int priority;
        private final int seq;

        private Entry(T renderer, int priority) {
            this.renderer = renderer
            this.priority = priority
            seq = RENDERER_SEQUENCE.incrementAndGet()
        }

        public int compareTo(Entry entry) {
            return priority == entry.priority ? entry.seq - seq : entry.priority - priority;
        }
    }
}
