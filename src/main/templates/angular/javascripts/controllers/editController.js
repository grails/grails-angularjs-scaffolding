//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}EditController", ${className}EditController);

function ${className}EditController(${className}, \$stateParams, \$state<%= injections ? ', ' + injections.keySet().join(', ') : '' %>) {
    var ${controllerAs} = this;

    <%= injections ? injections.values().join('\n    ') : '' %>

    ${className}.get({id: \$stateParams.id}, function(data) {
        ${controllerAs}.${propertyName} = new ${className}(data);
    }, function() {
        ${controllerAs}.errors = [{message: "Could not retrieve ${propertyName} with ID " + \$stateParams.id}];
    });

    ${controllerAs}.update${className} = function() {
        ${controllerAs}.errors = undefined;
        ${controllerAs}.${propertyName}.\$update(function() {
            \$state.go('${propertyName}.show', {id: ${controllerAs}.${propertyName}.id});
        }, function(response) {
            var data = response.data;
            if (data.hasOwnProperty('message')) {
                ${controllerAs}.errors = [data];
            } else {
                ${controllerAs}.errors = data._embedded.errors;
            }
        });
    };
}
