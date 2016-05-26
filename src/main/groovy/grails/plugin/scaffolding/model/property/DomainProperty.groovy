package grails.plugin.scaffolding.model.property
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

    Class getAssociatedType()

    PersistentEntity getAssociatedEntity()

    boolean isRequired()

    List<String> getLabelKeys()

    String getDefaultLabel()

}