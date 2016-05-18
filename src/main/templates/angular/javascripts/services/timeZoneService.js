//= wrapped

angular
    .module("${moduleName}")
    .factory("timeZoneService", timeZoneService);

function timeZoneService() {
    this.get = function () {
        return ${timeZones};
    }
}