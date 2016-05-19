//= wrapped

angular
    .module("${moduleName}")
    .factory("${className}", ${className});

function ${className}(\$resource<%= injections ? ', ' + injections.keySet().join(', ') : '' %>) {
    var ${className} = \$resource(
        "${propertyName}/:id",
        {"id": "@id"},
        {"update": {method: "PUT"},
         "query": {method: "GET", isArray: true<%= injections ? ", transformResponse: [angular.fromJson, " + injections.collect({ "transform${it.key}"}).join(', ') + ']' : '' %>},
         "get": {method: 'GET'<%= injections ? ", transformResponse: [angular.fromJson, " + injections.collect({ "convertTo${it.key}"}).join(', ') + ']' : '' %>}}
    );

    ${className}.list = ${className}.query;

    ${className}.prototype.toString = function() {
        return '${packageName}.${className} : ' + (this.id ? this.id : '(unsaved)');
    };
    <% injections.each { %>
    function convertTo${it.key}(${propertyName}) {
        ${propertyName}.${it.value} = new ${it.key}(${propertyName}.${it.value});
        return ${propertyName};
    }
    function transform${it.key}(${propertyName}List) {
        return ${propertyName}List.map(convertTo${it.key});
    }
    <% } %>
    return ${className};
}
