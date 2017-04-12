package com.example.gustavo.photostest.utils;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class BusProvider {
    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance(){
        return BUS;
    }

    private BusProvider(){ }
}
