package com.maximchuk.json;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Maxim L. Maximcuhk
 *         Date: 21.07.13
 */
public abstract class JsonDTO {

    public JsonDTO() {
    }

    public JsonDTO(JSONObject json) throws Exception {
        fromJSON(this, json);
    }

    public JSONObject toJSON() throws Exception {
        return toJSON(this);
    }

    private JSONObject toJSON(Object object) throws Exception {
        JSONObject json = new JSONObject();
        List<Field> declaredFields = getDeclaredFields(object.getClass());
        for (Field field: declaredFields) {
            if (field.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }
            String fieldName = field.getName();
            String jsonParamName = field.isAnnotationPresent(JsonParam.class)? field.getAnnotation(JsonParam.class).name(): fieldName;

            Object value = getGetterMethod(object.getClass(), field).invoke(object);
            if (value != null) {
                if (field.getType().isAssignableFrom(List.class)) {
                    List list = (List)value;
                    JSONArray jsonArray = new JSONArray();
                    for (Object obj: list) {
                        jsonArray.put(toJSON(obj));
                    }
                    json.put(jsonParamName, jsonArray);
                } else if (field.getType().getSuperclass() == JsonDTO.class) {
                    JsonDTO entity = (JsonDTO)value;
                    json.put(jsonParamName, entity.toJSON());
                } else if (field.getGenericType() == Date.class && field.isAnnotationPresent(JsonDateParam.class)) {
                    String dateParrern = field.getAnnotation(JsonDateParam.class).pattern();
                    json.put(jsonParamName, new SimpleDateFormat(dateParrern).format((Date)value));
                } else {
                    json.put(jsonParamName, value);
                }
            }
        }
        return json;
    }

    private final JsonDTO fromJSON(JsonDTO obj, JSONObject json) throws Exception {
        List<Field> declaredFields = getDeclaredFields(obj.getClass());
        for (Field field: declaredFields) {
            String fieldName =  field.getName();
            String jsonParamName = field.isAnnotationPresent(JsonParam.class)? field.getAnnotation(JsonParam.class).name(): fieldName;
            if (!json.has(jsonParamName)) {
                continue;
            }

            Method method = getSetterMethod(obj.getClass(), field);

            Object value = null;
            if (field.getGenericType() == int.class || field.getGenericType() == Integer.class) {
                value = json.getInt(jsonParamName);
            } else if (field.getGenericType() == long.class || field.getGenericType() == Long.class) {
                value = json.getLong(jsonParamName);
            } else if (field.getGenericType() == float.class || field.getGenericType() == Float.class
                    || field.getGenericType() == double.class || field.getGenericType() == Double.class) {
                value = json.getDouble(jsonParamName);
            } else if (field.getGenericType() == String.class) {
                value = json.getString(jsonParamName);
            } else if (field.getGenericType() == Boolean.class) {
                value = json.getBoolean(jsonParamName);
            } else if (field.getGenericType() == Date.class) {
                if (field.isAnnotationPresent(JsonDateParam.class)) {
                    String dateParrern = field.getAnnotation(JsonDateParam.class).pattern();
                    value = new SimpleDateFormat(dateParrern).parse(json.getString(jsonParamName));
                }
            } else if (field.getType().getSuperclass() == JsonDTO.class) {
                value = fromJSON((JsonDTO) field.getType().newInstance(), (json.getJSONObject(jsonParamName)));
            } else if (field.getType().isAssignableFrom(List.class)) {
                String listItemClassName = method.getGenericParameterTypes()[0].toString();
                listItemClassName = listItemClassName.substring(listItemClassName.indexOf("<") + 1, listItemClassName.indexOf(">"));
                Class clazz = Class.forName(listItemClassName);
                List list = new ArrayList();
                JSONArray jsonArray = json.getJSONArray(jsonParamName);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(fromJSON((JsonDTO)clazz.newInstance(), jsonArray.getJSONObject(i)));
                }
                method.invoke(obj, list);
            }

            if (value != null) {
                method.invoke(obj, value);
            }

        }
        return obj;
    }

    private List<Field> getDeclaredFields(Class clazz) {
        List<Field> fields = new ArrayList<Field>();
        if (clazz.getSuperclass() != JsonDTO.class) {
            fields.addAll(getDeclaredFields(clazz.getSuperclass()));
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    private Method getSetterMethod(Class clazz, Field field) throws NoSuchMethodException {
        Method method;
        try {
            String fieldName = field.getName();
            String methodName = "set" + fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
            method = clazz.getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != JsonDTO.class) {
                method = getSetterMethod(clazz.getSuperclass(), field);
            } else {
                throw ex;
            }
        }
        return method;
    }

    private Method getGetterMethod(Class clazz, Field field) throws NoSuchMethodException {
        Method method;
        try {
            String fieldName = field.getName();
            String methodName = "get" + fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
            method = clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != JsonDTO.class) {
                method = getGetterMethod(clazz.getSuperclass(), field);
            } else {
                throw ex;
            }
        }
        return method;
    }

}
