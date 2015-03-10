package com.maximchuk.json.stream;

import org.junit.Assert;
import org.junit.Test;

public class DefaultJSONStreamHandlerTest {

    private static final String TEST_JSON_STRING = "[array1, 'array2', { obj1 : {key1: 'value1', key2: 'value2'}, obj2 : {key3:value3}}}, {'otherKey': \"someValue\"}]";

    @Test
    public void testHandle() {
        try {
            TestJSONStreamHandler handler = new TestJSONStreamHandler(TEST_JSON_STRING);
            handler.parse();
            Assert.assertTrue(!handler.getResult().toString().isEmpty());
            System.out.println(handler.getResult().toString());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}