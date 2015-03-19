package com.maximchuk.json.stream;

import com.maximchuk.json.exception.JsonException;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            int i = 0;
            while (!end()) {
                c = next();
                i++;
                isSpecial = false;

                if (c == '"') {
                    if (!isText2) {
                        isText1 = !isText1;
                        continue;
                    }
                }
                if (c == '\'') {
                    if (!isText1) {
                        isText2 = !isText2;
                        continue;
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
                            isSpecial = false;
                        }
                    }
                }
                if (!isSpecial) {
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
            key = buf.substring(0, buf.lastIndexOf(':'));
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
                key = sbuf[0];
                value = sbuf[1];
            } else {
                value = sbuf[0];
            }
            cleanBuf();
            element(key, value);
        }
    }

    private char next() throws IOException {
        return (char)reader.read();
    }

    private boolean end() throws IOException {
        return !reader.ready();
    }

    public void startObject(String key) {}

    public void endObject() {}

    public void startArray(String key) {}

    public void endArray() {}

    public void element(String key, String value) {}

}
