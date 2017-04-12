package com.example.gustavo.photostest.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class BinObject implements Serializable {

    @SerializedName("data")
    public String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
