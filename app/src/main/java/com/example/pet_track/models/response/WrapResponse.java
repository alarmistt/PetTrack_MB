package com.example.pet_track.models.response;

public class WrapResponse<T> {
    private T data;
    private String message;
    private int statusCode;
    private String code;

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }
}
