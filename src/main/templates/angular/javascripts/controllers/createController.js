//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}CreateController", ${className}CreateController);

function ${className}CreateController(${className}, \$state<%= createParams ? ', \$stateParams' : '' %><%= injections ? ', ' + injections.keySet().join(', ') : '' %>) {

    var ${controllerAs} = this;
    <%= injections ? injections.values().join('\n    ') : '' %>
    ${controllerAs}.${propertyName} = new ${className}();
    <% createParams.each { %>
    if (\$stateParams.${it.parameterName}) {
        ${controllerAs}.${propertyName}.${it.propertyName} = {id: \$stateParams.${it.parameterName}};
    }
    <% } %>
    ${controllerAs}.save${className} = function() {
        ${controllerAs}.errors = undefined;
        ${controllerAs}.${propertyName}.\$save({}, function() {
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
