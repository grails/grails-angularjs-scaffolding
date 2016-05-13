//= wrapped

angular
    .module("${moduleName}")
    .controller("${className}CreateController", ${className}CreateController);

function ${className}CreateController(${className}, \$state) {
    var vm = this;

    vm.part = new ${className}();

    vm.save${className} = function() {
        vm.errors = [];
        vm.${propertyName}.\$save({}, function() {
            \$state.go('${propertyName}.list');
        }, function(response) {
            vm.errors = response.data;
        });
    };
}
