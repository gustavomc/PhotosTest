package com.example.gustavo.photostest.events;

import com.example.gustavo.photostest.models.BinObject;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class GetBinEvent {
    private boolean success;
    private String messageConfirm;
    private BinObject data;

    public GetBinEvent(boolean success, BinObject data, String messageConfirm) {
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

    public BinObject getData() {
        return data;
    }
}
