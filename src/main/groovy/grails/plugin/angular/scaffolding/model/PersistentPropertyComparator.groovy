package grails.plugin.angular.scaffolding.model

import grails.core.GrailsDomainClass
import grails.validation.Constrained
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.model.PersistentProperty
import org.springframework.util.Assert

@CompileStatic
class PersistentPropertyComparator implements Comparator {

    private Map constrainedProperties;
    private GrailsDomainClass domainClass;

    public PersistentPropertyComparator(GrailsDomainClass domainClass) {
        Assert.notNull(domainClass, "Argument 'domainClass' is required!")

        constrainedProperties = domainClass.constrainedProperties
        this.domainClass = domainClass
    }

    public int compare(Object o1, Object o2) {
        if (o1.equals(domainClass.identifier)) {
            return -1;
        }
        if (o2.equals(domainClass.identifier)) {
            return 1;
        }

        PersistentProperty prop1 = (PersistentProperty)o1;
        PersistentProperty prop2 = (PersistentProperty)o2;

        Constrained cp1 = (Constrained)constrainedProperties.get(prop1.name);
        Constrained cp2 = (Constrained)constrainedProperties.get(prop2.name);

        if (cp1 == null & cp2 == null) {
            return prop1.name.compareTo(prop2.name);
        }

        if (cp1 == null) {
            return 1;
        }

        if (cp2 == null) {
            return -1;
        }

        if (cp1.order > cp2.order) {
            return 1;
        }

        if (cp1.order < cp2.order) {
            return -1;
        }

        return 0;
    }
}
