//= wrapped

angular
    .module("${moduleName}")
    .service("currencyService", currencyService);

function currencyService() {
    this.get = function () {
        return ${currencies};
    }
}