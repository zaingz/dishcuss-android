package com.dishcuss.foodie.hub.Helper;

import com.squareup.otto.Bus;

/**
 * Created by Naeem Ibrahim on 7/28/2016.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }        private BusProvider() {
        // No instances.
    }
}
