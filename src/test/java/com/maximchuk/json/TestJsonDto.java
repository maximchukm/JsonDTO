package com.maximchuk.json;

import java.util.Date;

/**
 * Created by Maxim Maximchuk on 19.06.2015.
 */
public class TestJsonDto extends JsonDTO {

    private String string = "testString";

    private int integer = 3;

    private Date date = new Date();

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
