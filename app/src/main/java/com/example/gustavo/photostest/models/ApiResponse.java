package com.example.gustavo.photostest.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class ApiResponse<T> {
    @SerializedName("status")
    private Integer status = 0;
    @SerializedName("message")
    private String message = null;
    @SerializedName("data")
    private T data = null;

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }
}