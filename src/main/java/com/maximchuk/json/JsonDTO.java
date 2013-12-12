package com.maximchuk.json;

import com.maximchuk.json.annotation.JsonConverter;
import com.maximchuk.json.annotation.JsonDateParam;
import com.maximchuk.json.annotation.JsonIgnore;
import com.maximchuk.json.annotation.JsonParam;
import com.maximchuk.json.exception.JsonException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Maxim L. Maximcuhk
 *         Date: 21.07.13
 */
public abstract class JsonDTO {

    private static final String PERSISTENCE_FIELD = "_persistence_";

    private Set<String> ignoredFieldnameSet = new HashSet<String>();

    public JsonDTO() {
    }

    public JsonDTO(JSONObject json) throws JsonException {
        fromJSON(this, json);
    }

    public JSONObject toJSON() throws JsonException {
        return toJSON(this);
    }

    public void settingFromJson(JSONObject json) throws Exception {
        fromJSON(this, json);
    }

    public void addFieldToJsonIgnore(String fieldName) {
        ignoredFieldnameSet.add(fieldName);
    }

    private JSONObject toJSON(Object object) throws JsonException {
        JSONObject json = new JSONObject();
        try {
            List<Field> declaredFields = getDeclaredFields(object.getClass());
            for (Field field: declaredFields) {
                if (field.getName().contains(PERSISTENCE_FIELD)
                        || Modifier.isStatic(field.getModifiers())
                        || field.isAnnotationPresent(JsonIgnore.class)
                        || ignoredFieldnameSet.contains(field.getName())) {
                    continue;
                }
                String fieldName = field.getName();
                String jsonParamName = field.isAnnotationPresent(JsonParam.class)? field.getAnnotation(JsonParam.class).name(): fieldName;

                Object value = getGetterMethod(object.getClass(), fieldName).invoke(object);
                if (value != null) {
                    if (field.getType().isAssignableFrom(List.class)) {
                        List list = (List)value;
                        JSONArray jsonArray = new JSONArray();
                        for (Object obj: list) {
                            jsonArray.put(toJSON(obj));
                        }
                        json.put(jsonParamName, jsonArray);
                    } else if (field.getGenericType() == Date.class && field.isAnnotationPresent(JsonDateParam.class)) {
                        String dateParrern = field.getAnnotation(JsonDateParam.class).pattern();
                        json.put(jsonParamName, new SimpleDateFormat(dateParrern).format((Date)value));
                    } else if (isJsonDTOClass(field.getType())) {
                        if (field.isAnnotationPresent(JsonConverter.class)) {
                            JsonConverter converter = field.getAnnotation(JsonConverter.class);
                            Method method = getGetterMethod(field.getType(), converter.valueFieldName());
                            json.put(jsonParamName, method.invoke(value));
                        } else {
                            JsonDTO entity = (JsonDTO)value;
                            json.put(jsonParamName, entity.toJSON());
                        }
                    } else {
                        json.put(jsonParamName, value);
                    }
                }
            }
        } catch (Exception ex) {
            throw new JsonException(ex);
        }
        return json;
    }

    private final JsonDTO fromJSON(JsonDTO obj, JSONObject json) throws JsonException {
        try {
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
                } else if (field.getGenericType() == float.class || field.getGenericType() == Float.class) {
                    value = (float)json.getDouble(jsonParamName);
                } else if (field.getGenericType() == double.class || field.getGenericType() == Double.class) {
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
                } else if (field.getType().isEnum()) {
                    value = Enum.valueOf((Class<Enum>)field.getType(), json.getString(jsonParamName));
                } else if (isJsonDTOClass(field.getType())) {
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
        } catch (Exception ex) {
            throw new JsonException(ex);
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

    private Method getGetterMethod(Class clazz, String fieldName) throws NoSuchMethodException {
        Method method;
        try {
            String methodName = "get" + fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
            method = clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != JsonDTO.class) {
                method = getGetterMethod(clazz.getSuperclass(), fieldName);
            } else {
                throw ex;
            }
        }
        return method;
    }

    private boolean isJsonDTOClass(Class clazz) {
        boolean isJsonDTO = false;
        if (!clazz.isEnum() && !clazz.isInterface())  {
            isJsonDTO = clazz == JsonDTO.class;
            if (!isJsonDTO && clazz != Object.class) {
                isJsonDTO = isJsonDTOClass(clazz.getSuperclass());
            }
        }
        return isJsonDTO;
    }

}
