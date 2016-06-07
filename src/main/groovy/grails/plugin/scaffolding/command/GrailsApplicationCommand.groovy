package grails.plugin.scaffolding.command

import grails.dev.commands.ApplicationCommand
import grails.dev.commands.ExecutionContext
import grails.plugin.scaffolding.io.FileSystemInteraction
import grails.plugin.scaffolding.io.FileSystemInteractionImpl
import grails.plugin.scaffolding.angular.model.AngularModelBuilder
import grails.plugin.scaffolding.template.TemplateRenderer
import grails.plugin.scaffolding.template.TemplateRendererImpl


trait GrailsApplicationCommand implements ApplicationCommand, AngularModelBuilder {

    @Delegate TemplateRenderer templateRenderer
    @Delegate FileSystemInteraction fileSystemInteraction
    ExecutionContext executionContext

    boolean handle(ExecutionContext executionContext) {
        this.executionContext = executionContext
        this.templateRenderer = new TemplateRendererImpl(executionContext.baseDir)
        this.fileSystemInteraction = new FileSystemInteractionImpl(executionContext.baseDir)
        handle()
    }

    List<String> getArgs() {
        executionContext.commandLine.remainingArgs
    }

    abstract boolean handle()
}
