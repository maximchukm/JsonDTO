package com.maximchuk.json.rest.provider;

import com.maximchuk.json.JsonDTO;
import com.maximchuk.json.exception.JsonException;
import org.json.JSONObject;

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
 *         date 27.08.2014.
 */
@Provider
@Consumes("application/json")
@Produces("application/json")
public class JsonDtoProvider<T extends JsonDTO> extends AbstractProvider<T> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonDTO.class.isAssignableFrom(type);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        String json = readJsonString(entityStream);
        T jsonDto;
        try {
            jsonDto = type.newInstance();
            jsonDto.settingFromJson(new JSONObject(json));
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
        return jsonDto;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonDTO.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return getJsonString(t).getBytes().length;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, "UTF8");
        writer.write(getJsonString(t));
        writer.flush();
    }

    private String getJsonString(T jsonDto) {
        String json = "";
        try {
            json = jsonDto.toJSON().toString();
        } catch (JsonException e) {
            e.printStackTrace();
        }
        return json;
    }
}
