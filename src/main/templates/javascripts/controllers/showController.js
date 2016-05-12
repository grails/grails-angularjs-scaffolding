//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}ShowController", ${className}ShowController);

function ${className}ShowController(${className}, \$stateParams, \$state) {
    var vm = this;

    ${className}.get({id: \$stateParams.id}, function(data) {
        vm.${propertyName} = new ${className}(data);
    }, function() {
        \$state.go('${propertyName}.list');
    });

    vm.delete = function() {
        vm.${propertyName}.\$delete(function() {
            \$state.go('${propertyName}.list');
        }, function() {
            //on error
        })
    };

}
