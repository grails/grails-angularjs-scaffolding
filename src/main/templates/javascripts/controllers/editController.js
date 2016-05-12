//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}EditController", ${className}EditController);

function ${className}EditController(${className}, \$stateParams, \$state) {
    var vm = this;

    ${className}.get({id: \$stateParams.id}, function(data) {
        vm.${propertyName} = new ${className}(data);
    }, function() {
        vm.errors = [{message: "Could not retrieve ${propertyName} with ID " + \$stateParams.id}];
    });

    vm.update${className} = function() {
        vm.errors = [];
        vm.${propertyName}.\$update(function() {
            \$state.go('${propertyName}.list');
        }, function(response) {
            vm.errors = response.data;
        });
    };
}
