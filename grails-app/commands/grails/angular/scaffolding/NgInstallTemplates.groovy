package grails.angular.scaffolding

import groovy.io.FileType
import org.grails.io.support.Resource
import org.grails.io.support.SpringIOUtils
import org.grails.plugin.scaffolding.command.GrailsApplicationCommand

/**
 * Created by Jim on 6/17/2016.
 */
class NgInstallTemplates implements GrailsApplicationCommand {

    @Override
    boolean handle() {
        try {
            templates("angular/**/*").each { Resource r ->
                String path = r.URL.toString().replaceAll(/^.*?META-INF/, "src/main")
                if (path.endsWith('/')) {
                    mkdir(path)
                } else {
                    File to = new File(path)
                    SpringIOUtils.copy(r, to)
                    println("Copied ${r.filename} to location ${to.canonicalPath}")
                }
            }
        } catch (e) {
            println e
        }
    }

}
