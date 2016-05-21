//= wrapped

angular
    .module("${moduleName}")
    .factory("domainToManyConversion", domainToManyConversion);

function domainToManyConversion(\$injector) {
    var domainCache = {};
    return function(domainClass, property) {
        return function(domain) {
            var Domain;
            if (!domainCache[domainClass]) {
                domainCache[domainClass] = \$injector.get(domainClass);
            }
            Domain = domainCache[domainClass];
            domain[property] = domain[property].map(function(obj) {
                return new Domain(obj);
            });
            return domain;
        };
    };
}