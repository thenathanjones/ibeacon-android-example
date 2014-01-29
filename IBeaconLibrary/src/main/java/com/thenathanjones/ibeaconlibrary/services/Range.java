package com.thenathanjones.ibeaconlibrary.services;

/**
 * Created by thenathanjones on 29/01/2014.
 */
public class Range {
    public final long timestamp;
    public final double accuracy;

    public Range(long timestamp, double accuracy) {
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }

    public static Range from(long timestamp, int rssi, int txPower, Range lastRange) {
        double accuracy = accuracyFrom(rssi, txPower);

        if (lastRange != null) {
            accuracy = filteredAccuracy(accuracy, lastRange.accuracy);
        }

        return new Range(timestamp, accuracy);
    }

    private static double accuracyFrom(int rssi, int txPower) {
        if (rssi == 0) {
            return -1;
        }

        double ratio = ((double)rssi) / txPower;
        if (ratio < 1) {
            return Math.pow(ratio, 10);
        }
        else {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    public static final double BLEND_FACTOR = 0.1;
    private static double filteredAccuracy(double newAccuracy, double previousAccuracy) {
        return previousAccuracy * (1 - BLEND_FACTOR) + newAccuracy * BLEND_FACTOR;
    }
}
