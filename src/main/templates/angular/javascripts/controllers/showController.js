//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}ShowController", ${className}ShowController);

function ${className}ShowController(${className}, \$stateParams, \$state) {
    var ${controllerAs} = this;

    ${className}.get({id: \$stateParams.id}, function(data) {
        ${controllerAs}.${propertyName} = new ${className}(data);
    }, function() {
        \$state.go('${propertyName}.list');
    });

    ${controllerAs}.delete = function() {
        ${controllerAs}.${propertyName}.\$delete(function() {
            \$state.go('${propertyName}.list');
        }, function() {
            //on error
        });
    };

}
