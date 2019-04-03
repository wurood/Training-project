package com.example.wurood.myapplication;

import com.squareup.otto.Bus;

public class BusStation {
    private static Bus bus;

    public static Bus getBus() {
        if (bus == null) {
            bus = new Bus();
        }
        return bus;
    }

}
