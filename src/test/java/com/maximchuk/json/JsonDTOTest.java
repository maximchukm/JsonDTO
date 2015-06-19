package com.maximchuk.json;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Maxim Maximchuk on 19.06.2015.
 */
public class JsonDTOTest {

    @Test
    public void testDiff() throws Exception {
        try {
            TestJsonDto testJsonDto = new TestJsonDto();
            TestJsonDto toCompareJsonDto = new TestJsonDto();
            toCompareJsonDto.setDate(new Date(new Date().getTime() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)));
            JSONObject result = testJsonDto.diffJson(toCompareJsonDto);
            assertEquals(1, result.length());

            toCompareJsonDto.setString("Hello test");
            result = testJsonDto.diffJson(toCompareJsonDto);
            assertEquals(2, result.length());
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

}