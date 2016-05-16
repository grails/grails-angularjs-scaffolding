//= wrapped

angular
    .module("${moduleName}")
    .factory("${className}", ${className});

function ${className}(\$resource) {
    var ${className} = \$resource(
        "${propertyName}/:id",
        {"id": "@id"},
        {"save": {method: "POST", transformRequest: objectToFormData, headers: {'Content-Type': undefined}},
         "update": {method: "PUT", transformRequest: objectToFormData, headers: {'Content-Type': undefined}},
         "list": {method: "GET", isArray: true}}
    );
    return ${className};
}

function objectToFormData(obj) {
    var fd = new FormData();

    function populateFormData(obj, fd, parentProperty) {
        for(var property in obj) {
            if(obj.hasOwnProperty(property)) {
                if (parentProperty) {
                    property = parentProperty + "." + property;
                }
                var value = undefined;
                property.split('.').forEach(function(prop) {
                    value = value ? value[prop] : obj[prop];
                });
                // if the property is an object, but not a File,
                // use recursivity.
                if(typeof value === 'object' && !(value instanceof File) && !(value instanceof Date)) {
                    populateFormData(value, fd, property);
                } else {
                    if (value instanceof Date) {
                        fd.append(property, (function() {
                            return value.getUTCFullYear() + '-' +
                                (value.getUTCMonth() + 1) + '-' +
                                value.getUTCDate() + ' ' +
                                value.getUTCHours() + ':' +
                                value.getUTCMinutes() + ':' +
                                value.getUTCSeconds() + '.' +
                                value.getUTCMilliseconds() + ' UTC';
                        })())
                    } else {
                        fd.append(property, value);
                    }
                }
            }
        }
    }

    populateFormData(obj, fd);

    return fd;
}
