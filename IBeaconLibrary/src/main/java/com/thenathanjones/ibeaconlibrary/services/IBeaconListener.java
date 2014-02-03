package com.thenathanjones.ibeaconlibrary.services;

import java.util.Collection;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public interface IBeaconListener {
    void beaconsFound(Collection<IBeacon> beacons);
}
