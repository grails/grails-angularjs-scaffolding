package grails.plugin.angular.scaffolding.model

import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty

/**
 * Created by Jim on 5/12/2016.
 */
interface DomainModelService {

    List<GrailsDomainClassProperty> getEditableProperties(GrailsDomainClass domainClass)

    List<GrailsDomainClassProperty> getVisibleProperties(GrailsDomainClass domainClass)

}
