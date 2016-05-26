package grails.plugin.scaffolding.angular.model

import grails.codegen.model.Model
import grails.util.GrailsNameUtils
import org.codehaus.groovy.runtime.MetaClassHelper
import org.grails.io.support.FileSystemResource
import org.grails.io.support.GrailsResourceUtils
import org.grails.io.support.Resource


trait AngularModelBuilder {

    String basePath
    /**
     * A model for the given class name
     * @param className The class name
     *
     * @return The {@link grails.codegen.model.Model} instance
     */
    AngularModel model(Class cls) {
        return new AngularModelImpl(cls.getName(), basePath)
    }
    /**
     * A model for the given class name
     * @param className The class name
     *
     * @return The {@link Model} instance
     */
    AngularModel model(String className) {
        return new AngularModelImpl(className, basePath)
    }

    /**
     * A model for the given class name
     * @param className The class name
     *
     * @return The {@link Model} instance
     */
    AngularModel model(File file) {
        model(new FileSystemResource(file))
    }

    /**
     * A model for the given class name
     * @param className The class name
     *
     * @return The {@link Model} instance
     */
    AngularModel model(Resource resource) {
        def className = GrailsResourceUtils.getClassName(resource)
        model(className)
    }

    private static class AngularModelImpl implements AngularModel {
        final String className
        final String fullName
        final String propertyName
        final String packageName
        final String simpleName
        final String lowerCaseName
        final String packagePath
        final String moduleName
        final String modulePath
        final String basePath

        AngularModelImpl(String className, String basePath) {
            this.basePath = basePath
            this.className = MetaClassHelper.capitalize(GrailsNameUtils.getShortName(className))
            this.fullName = className
            this.propertyName = GrailsNameUtils.getPropertyName(className)
            this.packageName = GrailsNameUtils.getPackageName(className)
            this.packagePath = packageName.replace('.' as char, File.separatorChar).replaceAll('\\\\', '/')
            this.simpleName = this.className
            this.lowerCaseName = GrailsNameUtils.getScriptName(className)
            this.moduleName = "${this.packageName}.${this.propertyName}"
            this.modulePath = "${this.packagePath}/${this.propertyName}"
        }

        @Override
        File getFile() {
            new File("${basePath}/${this.modulePath}/${this.moduleName}.js")
        }

        @Override
        Boolean exists() {
            file.exists()
        }

        @Override
        AngularModel getParentModule() {
            File file = new File("${basePath}/${this.packagePath}/${this.packageName}.js")
            if (file.exists()) {
                new AngularModelImpl(this.packageName, this.basePath)
            } else {
                null
            }
        }

        @Override
        String getModelName() {
            propertyName
        }

        @Override
        String convention(String conventionName) {
            "${simpleName}${conventionName}"
        }

        @Override
        Map<String, Object> asMap() {
            [ moduleName: moduleName, modulePath: modulePath, className: className, fullName: fullName, propertyName: propertyName, modelName: propertyName, packageName: packageName, packagePath: packagePath, simpleName: simpleName, lowerCaseName: lowerCaseName]
        }
    }
}
