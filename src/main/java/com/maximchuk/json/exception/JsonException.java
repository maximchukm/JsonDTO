package com.maximchuk.json.exception;

/**
 * @author Maxim L. Maximchuk
 *         Date: 11/15/13
 */
public class JsonException extends Exception {

    public JsonException() {
        super();
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
