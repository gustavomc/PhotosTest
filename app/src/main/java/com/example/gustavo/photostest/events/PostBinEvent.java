package com.example.gustavo.photostest.events;

import com.example.gustavo.photostest.models.PostBinResponse;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class PostBinEvent {
    private boolean success;
    private String messageConfirm;
    private PostBinResponse data;

    public PostBinEvent(boolean success, PostBinResponse data, String messageConfirm) {
        this.success = success;
        this.messageConfirm = messageConfirm;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessageConfirm() {
        return messageConfirm;
    }

    public PostBinResponse getData() {
        return data;
    }
}
