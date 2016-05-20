package grails.plugin.angular.scaffolding.model.property
import grails.validation.Constrained
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty

interface DomainProperty extends PersistentProperty, Comparable<DomainProperty> {

    String getPathFromRoot()

    PersistentProperty getPersistentProperty()

    PersistentEntity getDomainClass()

    Constrained getConstraints()

    PersistentProperty getRootProperty()

    void setRootProperty(PersistentProperty rootProperty)

    Class getRootBeanType()

    Class getBeanType()

    boolean isRequired()

    List<String> getLabelKeys()

    String getDefaultLabel()

}