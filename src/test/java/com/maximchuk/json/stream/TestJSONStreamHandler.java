package com.maximchuk.json.stream;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Maxim Maximchuk
 *         date 10.03.15.
 */
public class TestJSONStreamHandler extends DefaultJSONStreamHandler {
    private JSONArray array;

    private String objKey;
    private JSONObject obj;

    public TestJSONStreamHandler(String s) {
        super(s);
    }

    @Override
    public void startObject(String key) {
        if (key != null) {
            objKey = key;
            obj.put(objKey, new JSONObject());
        } else {
            obj = new JSONObject();
        }
    }

    @Override
    public void endObject() {
        if (objKey != null) {
            objKey = null;
        } else {
            array.put(obj);
        }
    }

    @Override
    public void startArray(String key) {
        array = new JSONArray();
    }

    @Override
    public void element(String key, String value) {
        if (key != null) {
            if (objKey != null) {
                ((JSONObject)obj.get(objKey)).put(key, value);
            } else {
                obj.put(key, value);
            }
        } else {
            array.put(value);
        }
    }

    public JSONArray getResult() {
        return array;
    }
}
