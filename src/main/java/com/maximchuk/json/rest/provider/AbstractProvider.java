package com.maximchuk.json.rest.provider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Maxim Maximchuk
 *         date 22.08.2014.
 */
public abstract class AbstractProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {


    protected String readJsonString(InputStream entityStream) throws IOException {
        String json = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream, "UTF8"));
            json = reader.readLine();
            while (reader.ready()) {
                json += reader.readLine();
            }
        } finally {
            entityStream.close();
        }
        return json;
    }

}
