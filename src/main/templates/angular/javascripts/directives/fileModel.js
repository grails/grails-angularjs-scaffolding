//= wrapped

angular
    .module("${moduleName}")
    .directive("fileModel", fileModel);

function fileModel() {
    return {
        restrict: 'A',
        scope: {
            fileModel: '='
        },
        link: function (scope, element) {
            element.on('change', function() {
                scope.fileModel = element[0].files[0];
                scope.\$apply();
            });
        }
    };
}