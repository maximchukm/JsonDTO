package com.maximchuk.json.rest.provider;

import com.maximchuk.json.JsonDTO;
import com.maximchuk.json.exception.JsonException;
import org.json.JSONArray;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Maximchuk
 *         date 23.08.2014.
 */
@Provider
public class JsonDtoListProvider<T extends JsonDTO> extends AbstractProvider<List<T>> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == List.class && mediaType.equals(MEDIA_TYPE);
    }

    @Override
    public List<T> readFrom(Class<List<T>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        List resultList = new ArrayList();
        JSONArray jsonArray = new JSONArray(readJsonString(entityStream));
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Class jsonClass = getClass().getClassLoader().loadClass(((ParameterizedType) genericType).getActualTypeArguments()[0].toString().replace("class ", ""));
                JsonDTO jsonDTO = (JsonDTO)jsonClass.newInstance();
                jsonDTO.settingFromJson(jsonArray.getJSONObject(i));
                resultList.add(jsonDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }


    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(type) && isPresentJsonProducesAnntotation(annotations);
    }

    @Override
    public long getSize(List<T> list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return getJsonString(list).getBytes().length;
    }

    @Override
    public void writeTo(List<T> list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, "UTF8");
        writer.write(getJsonString(list));
        writer.flush();
    }

    private String getJsonString(List<T> list) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (T jsonDTO: list) {
                jsonArray.put(jsonDTO.toJSON());
            }
        } catch (JsonException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }
}