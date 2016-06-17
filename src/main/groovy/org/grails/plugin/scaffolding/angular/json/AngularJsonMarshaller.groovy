package org.grails.plugin.scaffolding.angular.json

import grails.converters.JSON
import org.grails.web.converters.exceptions.ConverterException
import org.grails.web.converters.marshaller.ObjectMarshaller
import org.grails.web.json.JSONWriter
import org.springframework.beans.BeanUtils

import javax.annotation.PostConstruct
import java.lang.reflect.Method

class AngularJsonMarshaller {

    protected void createEnumMarshaller() {
        JSON.registerObjectMarshaller(new ObjectMarshaller<JSON>() {
            public boolean supports(Object object) {
                return object.getClass().isEnum();
            }

            public void marshalObject(Object en, JSON json) throws ConverterException {
                JSONWriter writer = json.getWriter();
                try {
                    Class<?> enumClass = en.getClass();
                    Method nameMethod = BeanUtils.findDeclaredMethod(enumClass, "name", null);
                    try {
                        writer.value(nameMethod.invoke(en))
                    }
                    catch (Exception e) {
                        writer.value("")
                    }
                }
                catch (ConverterException ce) {
                    throw ce;
                }
                catch (Exception e) {
                    throw new ConverterException("Error converting Enum with class " + en.getClass().getName(), e);
                }
            }
        }, 0)
    }

    protected void createByteMarshaller() {
        JSON.registerObjectMarshaller(new ObjectMarshaller<JSON>() {
            public boolean supports(Object object) {
                object instanceof byte[];
            }

            public void marshalObject(Object object, JSON json) throws ConverterException {
                byte[] bytes = (byte[]) object;
                json.convertAnother(String.valueOf(bytes));
            }
        }, 0)
    }

    protected void createTimeZoneMarshaller() {
        JSON.registerObjectMarshaller(new ObjectMarshaller<JSON>() {
            public boolean supports(Object object) {
                object instanceof TimeZone
            }

            public void marshalObject(Object object, JSON json) throws ConverterException {
                TimeZone timeZone = (TimeZone) object;
                json.convertAnother(timeZone.ID);
            }
        }, 0)
    }

    @PostConstruct
    void appendMarshallers() {
        createEnumMarshaller()
        createByteMarshaller()
        createTimeZoneMarshaller()
    }
}
