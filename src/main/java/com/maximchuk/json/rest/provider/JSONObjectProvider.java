package com.maximchuk.json.rest.provider;

import org.json.JSONObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Maxim Maximchuk
 *         date 22.08.2014.
 */
@Provider
public class JSONObjectProvider extends AbstractProvider<JSONObject> {


    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == JSONObject.class && (isPresentJsonConsumesAnntotation(annotations) || mediaType.equals(MEDIA_TYPE));
    }

    @Override
    public JSONObject readFrom(Class<JSONObject> jsonObjectClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        return new JSONObject(readJsonString(inputStream));
    }

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == JSONObject.class && (isPresentJsonProducesAnntotation(annotations) || mediaType.equals(MEDIA_TYPE));
    }

    @Override
    public long getSize(JSONObject jsonObject, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return jsonObject.toString().getBytes().length;
    }

    @Override
    public void writeTo(JSONObject jsonObject, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF8");
        writer.write(jsonObject.toString());
        writer.flush();
    }

}
