package com.maximchuk.json.rest.provider;

import com.maximchuk.json.annotation.rest.JsonConsumes;
import com.maximchuk.json.annotation.rest.JsonProduces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;

/**
 * @author Maxim Maximchuk
 *         date 22.08.2014.
 */
public abstract class AbstractProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

    protected static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

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

    protected boolean isPresentJsonConsumesAnntotation(Annotation[] annotations) {
        boolean present = false;
        for (Annotation annotation: annotations) {
            present = annotation.annotationType() == JsonConsumes.class;
            if (present) {
                break;
            }
        }
        return present;
    }

    protected boolean isPresentJsonProducesAnntotation(Annotation[] annotations) {
        boolean present = false;
        for (Annotation annotation: annotations) {
            present = annotation.annotationType() == JsonProduces.class;
            if (present) {
                break;
            }
        }
        return present;
    }

}
