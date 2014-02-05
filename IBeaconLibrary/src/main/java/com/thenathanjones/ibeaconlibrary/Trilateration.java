package com.thenathanjones.ibeaconlibrary;

/**
 * Created by thenathanjones on 14/01/2014.
 */
public class Trilateration {
    public static Location findLocationFrom(IBeaconLocation beacon1, double distanceTo1, IBeaconLocation beacon2, double distanceTo2, IBeaconLocation beacon3, double distanceTo3) {
        Vector2 P1 = Vector2.of(beacon1.x / 100.0, beacon1.y / 100.0);
        Vector2 P2 = Vector2.of(beacon2.x / 100.0, beacon2.y / 100.0);
        Vector2 P3 = Vector2.of(beacon3.x / 100.0, beacon3.y / 100.0);

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
        double x = (Math.pow(distanceTo1, 2) - Math.pow(distanceTo2, 2) + Math.pow(d, 2))/(2*d);

        // y = ((pow(DistA,2) - pow(DistC,2) + pow(i,2) + pow(j,2))/(2*j)) - ((i/j)*x)
        double y = ((Math.pow(distanceTo1, 2) - Math.pow(distanceTo3, 2) + Math.pow(i, 2) + Math.pow(j, 2))/(2*j)) - ((i/j)*x);

        Vector2 result = P1.add(ex.multiply(x)).add(ey.multiply(y));

        return new Location(result.x, result.y);
    }
}
