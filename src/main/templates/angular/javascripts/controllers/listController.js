//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}ListController", ${className}ListController);

function ${className}ListController(${className}) {
    var ${controllerAs} = this;

    var max = 10, offset = 0;

    ${className}.list({max: max, offset: offset}, function(data) {
        ${controllerAs}.${propertyName}List = data;
    });
}
