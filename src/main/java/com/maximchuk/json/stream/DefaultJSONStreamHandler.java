package com.maximchuk.json.stream;

import com.maximchuk.json.exception.JsonException;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * SAX like JSON parser
 *
 * @author Maxim Maximchuk
 *         date 10.03.15.
 */
public class DefaultJSONStreamHandler {

    private InputStreamReader reader;
    private String buf = "";

    public DefaultJSONStreamHandler(InputStream inputStream) throws JSONException {
        reader = new InputStreamReader(inputStream);
    }

    public DefaultJSONStreamHandler(String s) {
        this(new ByteArrayInputStream(s.getBytes()));
    }

    public void parse() throws JsonException {
        boolean isSpecial;

        boolean isText1 = false;
        boolean isText2 = false;
        try {
            char c;
            while (!end()) {
                isSpecial = false;
                c = next();
                if (c == '"') {
                    if (!isText2) {
                        isText1 = !isText1;
                    }
                }
                if (c == '\'') {
                    if (!isText1) {
                        isText2 = !isText2;
                    }
                }
                if (!(isText1 | isText2)) {
                    isSpecial = true;
                    switch (c) {
                        case '{': {
                            startObject(extractKey());
                            break;
                        }
                        case '}': {
                            preparePrimitive();
                            endObject();
                            break;
                        }
                        case '[': {
                            startArray(extractKey());
                            break;
                        }
                        case ']': {
                            preparePrimitive();
                            endArray();
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
                    cleanBuf();
                } else {
                    buf += c;
                }
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }

    }

    private void cleanBuf() {
        buf = "";
    }

    private String extractKey() {
        String key = null;
        if (buf.length() > 0 && buf.trim().endsWith(":")) {
            key = parseKeyValue(buf)[0];
        }
        return key;
    }

    private void preparePrimitive() {
        if (buf.length() > 0) {
            String[] parsed = parseKeyValue(buf);
            if (parsed.length > 1) {
                objectPrimitive(parsed[0], parsed[1]);
            } else {
                arrayPrimitive(parsed[0]);
            }
        }
    }

    private String[] parseKeyValue(String string) {
        buf = buf.trim();
        char sh = 0;
        List<String> result = new ArrayList<String>(2);
        String s = "";
        for (char c : buf.toCharArray()) {
            if (c == '\'' || c == '"') {
                if (sh == c) {
                    sh = 0;
                } else if (sh == 0) {
                    sh = c;
                }
                continue;
            }
            if (sh == 0) {
                if (c == ':') {
                    result.add(s);
                    s = "";
                    continue;
                }
                if (c == 32) {
                    continue;
                }
            }
            s += c;
        }
        if (!s.isEmpty()) {
            result.add(s);
        }

        return result.toArray(new String[result.size()]);
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
