package com.thenathanjones.ibeacon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thenathanjones on 3/02/2014.
 */
public class LocationView extends SurfaceView implements SurfaceHolder.Callback {
    private TimerTask mDrawLoop;
    private SurfaceHolder mSurfaceHolder;
    private Timer mTimer = new Timer();
    private int mCanvasWidth;
    private int mCanvasHeight;
    private final List<IBeacon> mBeacons = new ArrayList<IBeacon>();

//    // Ze Office
//    private double mRoomWidth = 1085;
//    private double mRoomHeight = 680;
//    private final Map<String, IBeaconLocation> mBeaconLocations = new HashMap<String, IBeaconLocation>() {{
//        put("27760", IBeaconLocation.from(542, 10, 139)); // purple estimote
//        put("30397", IBeaconLocation.from(467, 438, 139)); // green estimote
//        put("55741", IBeaconLocation.from(84, 438, 139)); // blue estimote
//        put("300", IBeaconLocation.from(542, 10, 139)); // MacBeacon on pedro
//        put("240", IBeaconLocation.from(467, 438, 139)); // black bluecat
//        put("268", IBeaconLocation.from(84, 438, 139));  // white bluecat
//        put("229", IBeaconLocation.from(542, 10, 139));  // black bluecat
//    }};

    // Ze Office up high
    private double mRoomWidth = 1085;
    private double mRoomHeight = 680;
    private final Map<String, IBeaconLocation> mBeaconLocations = new HashMap<String, IBeaconLocation>() {{
        put("27760", IBeaconLocation.from(600, 0, 255)); // purple estimote
        put("30397", IBeaconLocation.from(300, 680, 255)); // green estimote
        put("55741", IBeaconLocation.from(100, 0, 255)); // blue estimote
        put("300", IBeaconLocation.from(542, 10, 255)); // MacBeacon on pedro
        put("240", IBeaconLocation.from(467, 438, 255)); // black bluecat
        put("268", IBeaconLocation.from(1000, 680, 255));  // white bluecat
        put("229", IBeaconLocation.from(542, 10, 255));  // black bluecat
    }};

//    // Heath and Kate's - kitchen:outside and garage:bannister
//    private double mRoomWidth = 667;
//    private double mRoomHeight = 443;
//    private final Map<String, IBeaconLocation> mBeaconLocations = new HashMap<String, IBeaconLocation>() {{
//        put("240", IBeaconLocation.from(667, 95, 1)); // black bluecat
//        put("268", IBeaconLocation.from(70, 115, 0.2));  // white bluecat
//        put("27760", IBeaconLocation.from(75, 350, 1)); // purple estimote
//        put("30397", IBeaconLocation.from(270, 0, 1)); // green estimote
//        put("55741", IBeaconLocation.from(663, 398, 1)); // blue estimote
//    }};

    // Home
//    private double mRoomWidth = 727;
//    private double mRoomHeight = 771;
//    private final Map<String, IBeaconLocation> mBeaconLocations = new HashMap<String, IBeaconLocation>() {{
//        put("55741", IBeaconLocation.from(597, 30, 1));
//        put("240", IBeaconLocation.from(487, 641, 1));
//        put("268", IBeaconLocation.from(230, 60, 0.2));
//        put("27760", IBeaconLocation.from(42, 270, 1));
//        put("30397", IBeaconLocation.from(1, 1, 1));
//    }};

    private Paint mLocationPaint;
    private Paint mRadiusPaint;
    private Paint mUserLocationPaint;
    private Paint mUserLabelPaint;
    private Paint mLabelPaint;
    private Location mExistingUserLocation;

    public LocationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setupPaints();
    }

    private void setupPaints() {
        mLocationPaint = new Paint();
        mLocationPaint.setAntiAlias(true);
        mLocationPaint.setARGB(255, 255, 255, 255);

        mUserLocationPaint = new Paint();
        mUserLocationPaint.setAntiAlias(true);
        mUserLocationPaint.setARGB(255, 255, 10, 10);

        mRadiusPaint = new Paint();
        mRadiusPaint.setAntiAlias(true);
        mRadiusPaint.setARGB(255, 255, 255, 255);
        mRadiusPaint.setStrokeWidth(2);
        mRadiusPaint.setStyle(Paint.Style.STROKE);

        mUserLabelPaint = new Paint();
        mUserLabelPaint.setAntiAlias(true);
        mUserLabelPaint.setARGB(255, 255, 10, 10);
        mUserLabelPaint.setTextSize(40);

        mLabelPaint = new Paint();
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setARGB(255, 255, 255, 255);
        mLabelPaint.setTextSize(40);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        mDrawLoop = new TimerTask() {
            @Override
            public void run() {
                drawLocation();
            }
        };

        mTimer.scheduleAtFixedRate(mDrawLoop, 0, 1000 / 10);
    }

    public void updateBeaconsWith(Collection<IBeacon> newBeacons) {
        synchronized (mSurfaceHolder) {
            mBeacons.clear();
            mBeacons.addAll(newBeacons);
        }
    }

    private void drawLocation() {
        Canvas canvas = null;
        try {
            canvas = mSurfaceHolder.lockCanvas(null);
            synchronized (mSurfaceHolder) {
                if (canvas != null) {

                    canvas.drawColor(Color.BLACK);

                    drawVisibleBeaconsOn(canvas);

                    drawCalculatedLocationOn(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawVisibleBeaconsOn(Canvas canvas) {
        for (IBeacon report : mBeacons) {
            IBeaconLocation beaconLocation = mBeaconLocations.get(report.minor + "");
            if (beaconLocation != null) {
                int x = translateX(beaconLocation.x);
                int y = translateY(beaconLocation.y);
                int radius = (int) (report.distanceInMetres() * 100.0 / mRoomWidth * mCanvasWidth);
                canvas.drawCircle(x, y, 5, mLocationPaint);
                canvas.drawCircle(x, y, radius, mRadiusPaint);

                DecimalFormat formatter = new DecimalFormat("#.##");
                canvas.drawText(formatter.format((double)radius / 100.0) + "m", x + radius/2, y + radius/2, mLabelPaint);
            }
        }
    }

    private int translateX(double x) {
        return (int) ((x / mRoomWidth) * mCanvasWidth);
    }

    private int translateY(double y) {
        return (int) ((y / mRoomHeight) * (mRoomHeight / mRoomWidth) * mCanvasWidth);
    }

    private void drawCalculatedLocationOn(Canvas canvas) {
        Collections.sort(mBeacons, new Comparator<IBeacon>() {
            @Override
            public int compare(IBeacon lhs, IBeacon rhs) {
                return lhs.distanceInMetres() > rhs.distanceInMetres() ? 1 : -1;
            }
        });

        if (mBeacons.size() > 2) {
            List<IBeacon> closestBeacons = mBeacons.subList(0, 3);
            List<IBeaconLocation> closestBeaconLocations = new ArrayList<IBeaconLocation>();
            for (IBeacon beacon : closestBeacons) {
                closestBeaconLocations.add(mBeaconLocations.get(beacon.minor + ""));
            }
            drawClosestBeaconsOn(canvas, closestBeaconLocations);

            Location userLocation = Trilateration.findLocationFrom(closestBeaconLocations, closestBeacons);

            if (mExistingUserLocation != null) {
                userLocation = filteredLocation(userLocation, mExistingUserLocation);
            }

            DecimalFormat formatter = new DecimalFormat("#.##");
            int x = translateX(userLocation.x * 100.0);
            int y = translateY(userLocation.y * 100.0);

            canvas.drawCircle(x, y, 20, mUserLocationPaint);
            canvas.drawText("x: " + formatter.format(userLocation.x) + ", y: " + formatter.format(userLocation.y), x + 50, y + 5, mUserLabelPaint);

            mExistingUserLocation = userLocation;
        }
    }

    private void drawClosestBeaconsOn(Canvas canvas, List<IBeaconLocation> closestBeaconLocations) {
        for (IBeaconLocation location : closestBeaconLocations) {
            canvas.drawCircle(translateX(location.x), translateY(location.y), 5, mUserLocationPaint);
        }
    }

    private Location filteredLocation(Location newLocation, Location previousLocation) {
        double x = previousLocation.x * (1 - IBeaconConstants.FILTER_FACTOR) + newLocation.x * IBeaconConstants.FILTER_FACTOR;
        double y = previousLocation.y * (1 - IBeaconConstants.FILTER_FACTOR) + newLocation.y * IBeaconConstants.FILTER_FACTOR;

        return new Location(x,y);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mDrawLoop.cancel();
    }
}
