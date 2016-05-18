//= wrapped

angular
    .module("${moduleName}")
    .service("localeService", localeService);

function localeService() {
    this.get = function () {
        return ${locales};
    }
}
