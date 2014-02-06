package com.thenathanjones.ibeaconlibrary.services;

import java.util.Arrays;
import java.util.List;

/**
 * Created by thenathanjones on 31/01/2014.
 */
public class IBeaconConstants {
    public static final int MAJOR_INDEX = 25;
    public static final int IBEACON_HEADER_INDEX = 5;
    public static final int PROXIMITY_UUID_INDEX = 9;
    public static final int MINOR_INDEX = 27;
    public static final int TXPOWER_INDEX = 29;
    public static final List<Integer> IBEACON_HEADER = Arrays.asList(0x4c, 0x00, 0x02, 0x15);
    public static final double BLEND_FACTOR = 0.1;
    final static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
}
