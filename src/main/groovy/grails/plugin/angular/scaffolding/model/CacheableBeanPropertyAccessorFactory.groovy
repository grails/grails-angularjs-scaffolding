package grails.plugin.angular.scaffolding.model

import grails.plugin.formfields.BeanPropertyAccessor
import grails.plugin.formfields.BeanPropertyAccessorFactory

/**
 * Created by Jim on 5/16/2016.
 */
class CacheableBeanPropertyAccessorFactory extends BeanPropertyAccessorFactory {

    private static Map<Object, Map<String, BeanPropertyAccessor>> propertyAccessorMap = [:]

    BeanPropertyAccessor accessorFor(bean, String propertyPath) {
        if (!propertyAccessorMap.containsKey(bean)) {
            propertyAccessorMap[bean] = [:]
        }

        if (!propertyAccessorMap[bean].containsKey(propertyPath)) {
            BeanPropertyAccessor propertyAccessor = super.accessorFor(bean, propertyPath)
            propertyAccessorMap[bean][propertyPath] = propertyAccessor
        }

        propertyAccessorMap[bean][propertyPath]
    }
}
