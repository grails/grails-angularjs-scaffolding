//= wrapped

angular
    .module("${moduleName}")
    .factory("${className}", ${className});

function ${className}(\$resource${injections}) {
    var ${className} = \$resource(
        "${uri}/:id",
        {"id": "@id"},
        {"update": {method: "PUT"},
         "query": {method: "GET", isArray: true${queryConfig}},
         "get": {method: 'GET'${getConfig}}}
    );

    ${className}.list = ${className}.query;

    ${className}.prototype.toString = function() {
        return '${packageName}.${className} : ' + (this.id ? this.id : '(unsaved)');
    };

    return ${className};
}
