package com.maximchuk.json.rest.provider;

import org.json.JSONArray;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
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
@Consumes("application/json")
@Produces("application/json")
public class JSONArrayProvider extends AbstractProvider<JSONArray> {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == JSONArray.class;
    }

    @Override
    public JSONArray readFrom(Class<JSONArray> jsonArrayClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        return new JSONArray(readJsonString(inputStream));
    }

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == JSONArray.class;
    }

    @Override
    public long getSize(JSONArray jsonArray, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return jsonArray.toString().getBytes().length;
    }

    @Override
    public void writeTo(JSONArray jsonArray, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF8");
        writer.write(jsonArray.toString());
        writer.flush();
    }

}
