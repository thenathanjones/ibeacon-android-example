package com.thenathanjones.ibeaconlibrary.services;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public class IBeacon {
    private static String TAG = IBeacon.class.getName();

    public final String uuid;
    public final int major;
    public final int minor;
    public final int txPower;

    public static final int IBEACON_HEADER_INDEX = 5;
    public static final int PROXIMITY_UUID_INDEX = 9;
    public static final int MAJOR_INDEX = 25;
    public static final int MINOR_INDEX = 27;
    public static final int TXPOWER_INDEX = 29;

    public static final List<Integer> IBEACON_HEADER = Arrays.asList(0x4c, 0x00, 0x02, 0x15);

    public IBeacon(String uuid, int major, int minor, int txPower) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.txPower = txPower;
    }

    public static IBeacon from(byte[] scanRecord) {
        String uuid = parseUUIDFrom(scanRecord);
        int major = (scanRecord[MAJOR_INDEX] & 0xff) * 0x100 + (scanRecord[MAJOR_INDEX+1] & 0xff);
        int minor = (scanRecord[MINOR_INDEX] & 0xff) * 0x100 + (scanRecord[MINOR_INDEX+1] & 0xff);
        int txPower = (int)scanRecord[TXPOWER_INDEX];

        Log.d(TAG, "UUID: " + uuid + "  Major: " + major + "  Minor: " + minor + "  TxPower: " + txPower);

        return new IBeacon(uuid, major, minor, txPower);
    }

    private final static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private static String parseUUIDFrom(byte[] scanRecord) {
        int[] proximityUuidBytes = new int[16];
        char[] proximityUuidChars = new char[proximityUuidBytes.length * 2];

        for (int i=0;i<proximityUuidBytes.length;i++) {
            proximityUuidBytes[i] = scanRecord[i+PROXIMITY_UUID_INDEX] & 0xFF;
            proximityUuidChars[i * 2] = hexArray[proximityUuidBytes[i] >>> 4];
            proximityUuidChars[i * 2 + 1] = hexArray[proximityUuidBytes[i] & 0x0F];
        }

        String proximityUuidHexString = new String(proximityUuidChars);
        StringBuilder builder = new StringBuilder();
        builder.append(proximityUuidHexString.substring(0, 8));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(8, 12));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(12, 16));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(16, 20));
        builder.append("-");
        builder.append(proximityUuidHexString.substring(20, 32));

        return builder.toString();
    }

    static boolean isBeacon(byte[] scanRecord) {
        Integer[] headerBytes = new Integer[9];

        for (int i=0;i<headerBytes.length;i++) {
            headerBytes[i] = scanRecord[i] & 0xff;
        }

        return Collections.indexOfSubList(Arrays.asList(headerBytes), IBEACON_HEADER) == IBEACON_HEADER_INDEX;
    }
}
