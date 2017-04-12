package com.example.gustavo.photostest.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class PostBinResponse implements Serializable {

    @SerializedName("uri")
    public String uri;

    public String getUri() {
        return uri;
    }

}