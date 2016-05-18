package grails.plugin.angular.scaffolding.command

import grails.build.logging.ConsoleLogger
import grails.build.logging.GrailsConsole
import grails.dev.commands.ApplicationCommand
import grails.dev.commands.ExecutionContext
import grails.plugin.angular.scaffolding.io.FileSystemInteraction
import grails.plugin.angular.scaffolding.io.FileSystemInteractionImpl
import grails.plugin.angular.scaffolding.model.AngularModelBuilder
import grails.plugin.angular.scaffolding.templates.TemplateRenderer
import grails.plugin.angular.scaffolding.templates.TemplateRendererImpl


trait GrailsApplicationCommand implements ApplicationCommand, AngularModelBuilder {

    @Delegate TemplateRenderer templateRenderer
    @Delegate ConsoleLogger consoleLogger
    @Delegate FileSystemInteraction fileSystemInteraction
    ExecutionContext executionContext

    boolean handle(ExecutionContext executionContext) {
        this.executionContext = executionContext
        this.consoleLogger = GrailsConsole.getInstance()
        this.templateRenderer = new TemplateRendererImpl(executionContext.baseDir, consoleLogger)
        this.fileSystemInteraction = new FileSystemInteractionImpl(executionContext.baseDir)
        handle()
    }

    List<String> getArgs() {
        executionContext.commandLine.remainingArgs
    }

    abstract boolean handle()
}
