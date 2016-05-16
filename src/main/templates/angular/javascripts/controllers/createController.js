//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}CreateController", ${className}CreateController);

function ${className}CreateController(${className}, \$state) {
    var ${controllerAs} = this;

    ${controllerAs}.part = new ${className}();

    ${controllerAs}.save${className} = function() {
        ${controllerAs}.errors = [];
        ${controllerAs}.${propertyName}.\$save({}, function() {
            \$state.go('${propertyName}.list');
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
