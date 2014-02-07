package com.thenathanjones.ibeaconlibrary;

import com.thenathanjones.ibeaconlibrary.services.IBeacon;

import java.util.List;

/**
 * Created by thenathanjones on 14/01/2014.
 */
public class Trilateration {
    public static Location findLocationFrom(List<IBeaconLocation> beaconLocations, List<IBeacon> beacons) {
        Vector2 P1 = Vector2.of(beaconLocations.get(0).x / 100.0, beaconLocations.get(0).y / 100.0);
        Vector2 P2 = Vector2.of(beaconLocations.get(1).x / 100.0, beaconLocations.get(1).y / 100.0);
        Vector2 P3 = Vector2.of(beaconLocations.get(2).x / 100.0, beaconLocations.get(2).y / 100.0);

        // ex = (P2 - P1)/(numpy.linalg.norm(P2 - P1))
        Vector2 ex = P2.subtract(P1).toUnitVector();

        // i = dot(ex, P3 - P1)
        double i = ex.dotProduct(P3.subtract(P1));

        // ey = (P3 - P1 - i*ex)/(numpy.linalg.norm(P3 - P1 - i*ex))
        Vector2 ey = P3.subtract(P1).subtract(ex.multiply(i)).toUnitVector();

        // d = numpy.linalg.norm(P2 - P1)
        double d = P2.distanceFrom(P1);

        // j = dot(ey, P3 - P1)
        double j = ey.dotProduct(P3.subtract(P1));

        // x = (pow(DistA,2) - pow(DistB,2) + pow(d,2))/(2*d)
        double x = (Math.pow(beacons.get(0).accuracyInMetres, 2) - Math.pow(beacons.get(1).accuracyInMetres, 2) + Math.pow(d, 2))/(2*d);

        // y = ((pow(DistA,2) - pow(DistC,2) + pow(i,2) + pow(j,2))/(2*j)) - ((i/j)*x)
        double y = ((Math.pow(beacons.get(0).accuracyInMetres, 2) - Math.pow(beacons.get(2).accuracyInMetres, 2) + Math.pow(i, 2) + Math.pow(j, 2))/(2*j)) - ((i/j)*x);

        Vector2 result = P1.add(ex.multiply(x)).add(ey.multiply(y));

        return new Location(result.x, result.y);
    }
}
