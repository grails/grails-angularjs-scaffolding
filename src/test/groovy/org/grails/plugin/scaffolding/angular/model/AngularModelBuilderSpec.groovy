package org.grails.plugin.scaffolding.angular.model

import spock.lang.Specification

/**
 * Created by Jim on 8/25/2016.
 */
class AngularModelBuilderSpec extends Specification implements AngularModelBuilder {
    
    void "test getFile"() {
        given:
        basePath = "src/test/resources"

        expect:
        model("com.foo.Bar").modulePath == "com/foo/bar"
        model("com.Foo").modulePath == "com/foo"
        model("Com").modulePath == "com"

        model("com.foo.Bar").moduleName == "com.foo.bar"
        model("com.Foo").moduleName == "com.foo"
        model("Com").moduleName == "com"

        model("com.foo.Bar").packagePath == "com/foo"
        model("com.Foo").packagePath == "com"
        model("Com").packagePath == ""

        model("com.foo.Bar").packageName == "com.foo"
        model("com.Foo").packageName == "com"
        model("Com").packageName == ""

        model("com.foo.Bar").modelName == "bar"
        model("com.Foo").modelName == "foo"
        model("Com").modelName == "com"

        model("com.foo.Bar").parentModule.file.exists()
        model("com.foo.Foo").parentModule.file.exists()
        model("com.Foo").parentModule.file.exists()
        model("com.Bar").parentModule.file.exists()
        model("Com").parentModule == null
    }
}
