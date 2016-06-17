package org.grails.plugin.scaffolding.angular.template

import grails.codegen.model.ModelBuilder
import org.grails.plugin.scaffolding.angular.template.AngularModuleEditor
import org.grails.plugin.scaffolding.angular.template.AngularModuleEditorImpl
import spock.lang.Shared
import spock.lang.Specification

class AngularModuleEditorSpec extends Specification implements ModelBuilder {

    @Shared
    File file

    @Shared
    AngularModuleEditor editor

    @Shared
    String lineEnding = System.getProperty('line.separator')

    void setup() {
        file = File.createTempFile("angularModule", "js")
        editor = new AngularModuleEditorImpl()
    }

    void cleanup() {
        file.delete()
    }

    void "test addDependency"() {
        given:
        file.write("""
            angular.module('x',[]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',[
    "hello"
]);
        """
        success
    }

    void "test addDependency with annotations"() {
        given:
        file.write("""
            angular.module('x',[]).controller("Hello", ["\$scope, function(\$scope) {}]);;
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',[
    "hello"
]).controller("Hello", ["\$scope, function(\$scope) {}]);;
        """
        success
    }

    void "test addDependency with variable in list"() {
        given:
        file.write("""
            var foo = "bar";
            angular.module('x',[foo]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then: "fails because it isn't valid json"
        file.text == """
            var foo = "bar";
            angular.module('x',[foo]);
        """
        !success
    }

    void "test addDependency with array before module"() {
        given:
        file.write("""
            var x = [1,2,3];
            angular.module('x',[]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then: "the correct array is updated"
        file.text == """
            var x = [1,2,3];
            angular.module('x',[
    "hello"
]);
        """
        success
    }

    void "test addDependency with array after module"() {
        given:
        file.write("""
            angular.module('x',[]);
            var x = [1,2,3];
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then: "the correct array is updated"
        file.text == """
            angular.module('x',[
    "hello"
]);
            var x = [1,2,3];
        """
        success
    }

    void "test addDependency wont duplicate dependencies"() {
        given:
        file.write("""
            angular.module('x',["hello"]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',["hello"]);
        """
        success
    }

    void "test addDependency with text before and after"() {
        given:
        file.write("""
            foo

            angular.module('x',[]);
            bar

        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            foo

            angular.module('x',[
    "hello"
]);
            bar

        """
        success
    }

    void "test addDependency with multiple modules"() {
        given:
        file.write("""
            angular.module('w', []);
            angular.module('x',[]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then: "the first one will be updated"
        file.text == """
            angular.module('w', [
    "hello"
]);
            angular.module('x',[]);
        """
        success
    }

    void "test addDependency with a single space before dependencies"() {
        given:
        file.write("""
            angular.module('x', []);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x', [
    "hello"
]);
        """
        success
    }

    void "test addDependency with a multiple spaces before dependencies"() {
        given:
        file.write("""
            angular.module('x',   []);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',   [
    "hello"
]);
        """
        success
    }

    void "test addDependency with invalid module but valid dependency section"() {
        given:
        file.write("""
            angular.module'x',[]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module'x',[
    "hello"
]);
        """
        success
    }

    void "test addDependency with invalid module dependency"() {
        given:
        file.write("""
            angular.module('x',]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',]);
        """
        !success
    }

    void "test addDependency with invalid module dependency 2"() {
        given:
        file.write("""
            angular.module('x',[);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',[);
        """
        !success
    }

    void "test addDependency with invalid module dependency 3"() {
        given:
        file.write("""
            angular.module('x',[);]
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',[);]
        """
        !success
    }

    void "test addDependency with no existing module"() {
        given:
        file.write("""
            hello
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            hello
        """
        !success
    }

    void "test addDependency with a trailing comma"() {
        given:
        file.write("""
            angular.module('x',["foo",]);
        """)

        when:
        boolean success = editor.addDependency(file, "hello")

        then:
        file.text == """
            angular.module('x',[
    "foo",
    "hello"
]);
        """
        success
    }

    void "test addRequire"() {
        given:
        file.write("""
            //= require /angular/angular
            //= require /angular/mock\nangular.module('x', []);
        """)

        when:
        boolean success = editor.addRequire(file, "/my/app")

        then:
        file.text == """
            //= require /angular/angular
            //= require /angular/mock\n//= require /my/app${lineEnding}angular.module('x', []);
        """
        success
    }

    void "test addRequire with extra lines after last require"() {
        given:
        file.write("""
            //= require /angular/angular
            //= require /angular/mock\n\nangular.module('x', []);
        """)

        when:
        boolean success = editor.addRequire(file, "/my/app")

        then:
        file.text == """
            //= require /angular/angular
            //= require /angular/mock\n//= require /my/app${lineEnding}\nangular.module('x', []);
        """
        success
    }


    void "test addRequire windows line endings"() {
        given:
        file.write("""
            //= require /angular/angular\r\n//= require /angular/mock\r\nangular.module('x', []);
        """)

        when:
        boolean success = editor.addRequire(file, "/my/app")

        then:
        file.text == """
            //= require /angular/angular\r\n//= require /angular/mock\r\n//= require /my/app${lineEnding}angular.module('x', []);
        """
        success
    }

    void "test addRequire no requires exist"() {
        given:
        file.write("""
            angular.module('x', []);
        """)

        when:
        boolean success = editor.addRequire(file, "/my/app")

        then: "it will add it to the beginning of the file"
        file.text == """//= require /my/app${lineEnding}
            angular.module('x', []);
        """
        success
    }

    void "test addRequire with require self"() {
        given:
        file.write("""
            //= require /angular/angular
            //= require_self
            //= require /angular/mock
            angular.module('x', []);
        """)

        when:
        boolean success = editor.addRequire(file, "/my/app")

        then: "it will add it before require_self"
        file.text == """
            //= require /angular/angular
            //= require /my/app${lineEnding}//= require_self
            //= require /angular/mock
            angular.module('x', []);
        """
        success
    }

    void "test addRequire with require self windows line endings"() {
        given:
        file.write("""
            //= require /angular/angular\r\n//= require_self\r\n//= require /angular/mock\r\nangular.module('x', []);
        """)

        when:
        boolean success = editor.addRequire(file, "/my/app")

        then:
        file.text == """
            //= require /angular/angular\r\n//= require /my/app${lineEnding}//= require_self\r\n//= require /angular/mock\r\nangular.module('x', []);
        """
        success
    }

    void "test addRequire wont duplicate requires"() {
        given:
        file.write("""
            //= require /angular/angular
            //= require_self
            //= require /angular/mock
            angular.module('x', []);
        """)

        when:
        boolean success = editor.addRequire(file, "/angular/mock")

        then:
        file.text == """
            //= require /angular/angular
            //= require_self
            //= require /angular/mock
            angular.module('x', []);
        """
        success
    }

    void "test addDependency with model"() {
        given:
        def model = model("test.foo.Bar")
        file.write("""
            //= require /angular/angular
            //= require /angular/mock\nangular.module('x', []);
        """)

        when:
        boolean success = editor.addDependency(file, model)

        then:
        file.text == """
            //= require /angular/angular
            //= require /angular/mock\n//= require /test/foo/bar/test.foo.bar${lineEnding}angular.module('x', [
    "test.foo.bar"
]);
        """
        success
    }

    void "test addDependency with model with invalid module definition"() {
        given:
        def model = model("test.foo.Bar")
        file.write("""
            //= require /angular/angular
            //= require /angular/mock\nangular.module('x', ]);
        """)

        when:
        boolean success = editor.addDependency(file, model)

        then: "the file is unchanged"
        file.text == """
            //= require /angular/angular
            //= require /angular/mock\nangular.module('x', ]);
        """
        !success
    }
}
