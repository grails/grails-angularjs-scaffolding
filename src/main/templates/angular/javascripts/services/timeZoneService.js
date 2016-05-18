//= wrapped

angular
    .module("${moduleName}")
    .service("timeZoneService", timeZoneService);

function timeZoneService() {
    this.get = function () {
        return ${timeZones};
    }
}