package com.maximchuk.json.stream;

import org.json.JSONException;
import org.json.JSONTokener;

import java.io.InputStream;

/**
 * SAX like JSON parser
 *
 * @author Maxim Maximchuk
 *         date 10.03.15.
 */
public class DefaultJSONStreamHandler extends JSONTokener {

    String buf = "";

    public DefaultJSONStreamHandler(InputStream inputStream) throws JSONException {
        super(inputStream);
    }

    public DefaultJSONStreamHandler(String s) {
        super(s);
    }

    public void parse() {
        char c = next();
        while (!end()) {
            switch (c) {
                case '{': {
                    startObject(extractKey());
                    break;
                }
                case '}': {
                    checkAndSendElement();
                    endObject();
                    break;
                }
                case '[': {
                    startArray(extractKey());
                    break;
                }
                case ']': {
                    checkAndSendElement();
                    endArray();
                    break;
                }
                case ',': {
                    checkAndSendElement();
                    break;
                }
                default: {
                    buf += c;
                }
            }
            c = next();
        }

    }

    private String cleanJsonElementString(String string) {
        return string.trim().replace("\'", "").replace("\"", "");
    }

    private void cleanBuf() {
        buf = "";
    }

    private String extractKey() {
        String key = null;
        if (buf.length() > 0 && buf.trim().endsWith(":")) {
            key = cleanJsonElementString(buf.substring(0, buf.lastIndexOf(':')));
            cleanBuf();
        }
        return key;
    }

    private void checkAndSendElement() {
        if (buf.length() > 0) {
            String key = null;
            String value;
            String[] sbuf = buf.split(":");
            if (sbuf.length > 1) {
                key = cleanJsonElementString(sbuf[0]);
                value = cleanJsonElementString(sbuf[1]);
            } else {
                value = cleanJsonElementString(sbuf[0]);
            }
            cleanBuf();
            element(key, value);
        }
    }

    public void startObject(String key) {}

    public void endObject() {}

    public void startArray(String key) {}

    public void endArray() {}

    public void element(String key, String value) {}

}
