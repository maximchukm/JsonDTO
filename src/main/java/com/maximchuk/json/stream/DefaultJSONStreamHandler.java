package com.maximchuk.json.stream;

import com.maximchuk.json.exception.JsonException;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Event based stream JSON parser
 *
 * @author Maxim Maximchuk
 *         date 10.03.15.
 */
public class DefaultJSONStreamHandler {

    private InputStreamReader reader;
    private String key = null;
    private String buf = "";

    public DefaultJSONStreamHandler(InputStream inputStream) throws JSONException {
        reader = new InputStreamReader(inputStream);
    }

    public DefaultJSONStreamHandler(String s) {
        this(new ByteArrayInputStream(s.getBytes()));
    }

    public void parse() throws JsonException {
        boolean isSpecial;
        char sh = 0;
        try {
            char c;
            while (!end()) {
                isSpecial = false;
                c = next();
                if (c == '\'' || c == '"') {
                    if (sh == c) {
                        sh = 0;
                        continue;
                    } else if (sh == 0) {
                        sh = c;
                        continue;
                    }
                }
                if (sh == 0) {
                    isSpecial = true;
                    switch (c) {
                        case '{': {
                            startObject(key);
                            key = null;
                            break;
                        }
                        case '}': {
                            preparePrimitive();
                            endObject();
                            break;
                        }
                        case '[': {
                            startArray(key);
                            key = null;
                            break;
                        }
                        case ']': {
                            preparePrimitive();
                            endArray();
                            break;
                        }
                        case ':': {
                            key = buf;
                            break;
                        }
                        case ',': {
                            preparePrimitive();
                            break;
                        }
                        default: {
                            isSpecial = false;
                        }
                    }
                }
                if (isSpecial) {
                    buf = "";
                } else if (!(sh == 0 && c == 32)) {
                    buf += c;
                }
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }

    }

    private void preparePrimitive() {
        if (!buf.isEmpty()) {
            if (key != null) {
                objectPrimitive(key, buf);
                key = null;
            } else {
                arrayPrimitive(buf);
            }
        }
    }

    private char next() throws IOException {
        return (char) reader.read();
    }

    private boolean end() throws IOException {
        return !reader.ready();
    }

    public void startObject(String key) {
    }

    public void endObject() {
    }

    public void startArray(String key) {
    }

    public void endArray() {
    }

    public void objectPrimitive(String key, String value) {
    }

    public void arrayPrimitive(String value) {
    }

}
