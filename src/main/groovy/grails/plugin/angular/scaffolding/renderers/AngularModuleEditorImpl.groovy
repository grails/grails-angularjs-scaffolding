package grails.plugin.angular.scaffolding.renderers

import grails.codegen.model.Model
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import java.util.regex.Matcher

@Slf4j
class AngularModuleEditorImpl implements AngularModuleEditor {

    boolean addDependency(File module, String dependency) {
        try {
            StringBuilder sb = new StringBuilder()
            String moduleText = module.text

            Matcher group = (moduleText =~ /(angular\.module)(.*),(\s*)(\[)/)
            String moduleDefinition = group[0][0]
            int startingIndex = moduleText.indexOf(moduleDefinition) + moduleDefinition.size() - 1
            sb.append(moduleText.substring(0, startingIndex))

            String temp = moduleText.substring(startingIndex)
            int endingIndex = temp.indexOf(']')+1
            //Remove trailing commas in the array
            temp = temp.substring(0, endingIndex).replaceAll(/(?m),(\p{C}|\s)*\]$/, "]")
            def json = new JsonSlurper().parseText(temp)
            if (!json.contains(dependency)) {
                json.add(dependency)
                sb.append(JsonOutput.prettyPrint(JsonOutput.toJson(json)))
                sb.append(moduleText.substring(startingIndex+endingIndex))

                module.write(sb.toString())
            }
            true
        } catch (Exception e) {
            log.error("Could not add $dependency dependency to module $module.name", e)
            false
        }
    }

    boolean addRequire(File module, String require) {
        try {
            StringBuilder sb = new StringBuilder()
            String moduleText = module.text
            Matcher group = (moduleText =~ /\/\/=\s*require.*(\r\n|\n|\r)?/)
            int index = 0
            if (group.any { it[0] =~ /\/\/=\s*require\s*${require}/ }) {
                return true
            }
            if (group.size() > 0) {
                for (int i = 0; i < group.size(); i++) {
                    String requireMatch = group[i][0]
                    if (requireMatch =~ /\/\/=\s*require_self/) {
                        index = moduleText.indexOf(requireMatch)
                        break;
                    }
                    if (i == group.size() -1) {
                        index = moduleText.indexOf(requireMatch) + requireMatch.size()
                    }
                }
                sb.append(moduleText.substring(0, index))
            }
            sb.append("//= require ${require}" + System.getProperty("line.separator"))
            sb.append(moduleText.substring(index))
            module.write(sb.toString())
            true
        } catch (Exception e) {
            log.error("Could not add asset require ${require} to module ${module.name}", e)
            false
        }
    }

    boolean addDependency(File module, Model model) {
        String originalText = module.text
        String moduleName = "${model.packageName}.${model.propertyName}"
        if (addDependency(module, moduleName) &&
            addRequire(module, "/${model.packagePath.replaceAll('\\\\','/')}/${model.propertyName}/${moduleName}")) {
            true
        } else {
            module.write(originalText)
            false
        }
    }
}
