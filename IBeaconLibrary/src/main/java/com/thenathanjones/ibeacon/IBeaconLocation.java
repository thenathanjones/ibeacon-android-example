package com.thenathanjones.ibeacon;

/**
 * Created by thenathanjones on 3/02/2014.
 */
public class IBeaconLocation {
    public final double x;
    public final double y;
    public final double z;

    private IBeaconLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static IBeaconLocation from(double x, double y, double z) {
        return new IBeaconLocation(x,y,z);
    }
}
