//= wrapped
//= require ${angularPath} ${'\n' + dependencies.collect{ '//= require ' + it.value }.join('\n') }
//= require_self
//= require_tree services
//= require_tree controllers
//= require_tree directives
//= require_tree domain
//= require_tree templates

angular.module("${moduleName}", [${dependencies.keySet().join(', ')}]).config(config);

function config(\$stateProvider) {
    \$stateProvider
        .state('${propertyName}', {
            url: "/${propertyName}",
            abstract: true,
            template: "<div ui-view></div>"
        })
        .state('${propertyName}.list', {
            url: "",
            templateUrl: "/${modulePath}/list.html",
            controller: "${className}ListController as ${controllerAs}"
        })
        .state('${propertyName}.create', {
            url: "/create",${createParams ? ("\n            params: " + createParams + ",") : ""}
            templateUrl: "/${modulePath}/create.html",
            controller: "${className}CreateController as ${controllerAs}"
        })
        .state('${propertyName}.edit', {
            url: "/edit/:id",
            templateUrl: "/${modulePath}/edit.html",
            controller: "${className}EditController as ${controllerAs}"
        })
        .state('${propertyName}.show', {
            url: "/show/:id",
            templateUrl: "/${modulePath}/show.html",
            controller: "${className}ShowController as ${controllerAs}"
        });
}
