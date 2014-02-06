package com.thenathanjones.ibeaconlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thenathanjones.ibeaconlibrary.services.IBeacon;
import com.thenathanjones.ibeaconlibrary.services.IBeaconConstants;

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

    // Heath and Kate's - kitchen:outside and garage:bannister
    private double mRoomWidth = 667;
    private double mRoomHeight = 443;
    private final Map<String, IBeaconLocation> mBeaconLocations = new HashMap<String, IBeaconLocation>() {{
        put("240", IBeaconLocation.from(667, 95, 1)); // black bluecat
        put("268", IBeaconLocation.from(70, 115, 0.2));  // white bluecat
        put("27760", IBeaconLocation.from(75, 350, 1)); // purple estimote
        put("30397", IBeaconLocation.from(270, 0, 1)); // green estimote
        put("55741", IBeaconLocation.from(663, 398, 1)); // blue estimote
    }};

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

        mLabelPaint = new Paint();
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setARGB(255, 255, 10, 10);
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

        mTimer.scheduleAtFixedRate(mDrawLoop, 0, 1000 / 20);
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
                int radius = (int) (report.accuracyInMetres * 100.0 / mRoomWidth * mCanvasWidth);
                canvas.drawCircle(x, y, 5, mLocationPaint);
                canvas.drawCircle(x, y, radius, mRadiusPaint);
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
                return lhs.accuracyInMetres > rhs.accuracyInMetres ? 1 : -1;
            }
        });

        if (mBeacons.size() > 2) {
            List<IBeacon> closestBeacons = mBeacons.subList(0, 3);

            IBeaconLocation location1 = mBeaconLocations.get(closestBeacons.get(0).minor + "");
            double distance1 = closestBeacons.get(0).accuracyInMetres;
            canvas.drawCircle(translateX(location1.x), translateY(location1.y), 5, mUserLocationPaint);
            IBeaconLocation location2 = mBeaconLocations.get(closestBeacons.get(1).minor + "");
            double distance2 = closestBeacons.get(1).accuracyInMetres;
            canvas.drawCircle(translateX(location2.x), translateY(location2.y), 5, mUserLocationPaint);
            IBeaconLocation location3 = mBeaconLocations.get(closestBeacons.get(2).minor + "");
            double distance3 = closestBeacons.get(2).accuracyInMetres;
            canvas.drawCircle(translateX(location3.x), translateY(location3.y), 5, mUserLocationPaint);

            Location userLocation = Trilateration.findLocationFrom(location1, distance1,
                                                                   location2, distance2,
                                                                   location3, distance3);

            if (mExistingUserLocation != null) {
                userLocation = filteredLocation(userLocation, mExistingUserLocation);
            }

            int x = translateX(userLocation.x * 100.0);
            int y = translateY(userLocation.y * 100.0);
            canvas.drawCircle(x, y, 20, mUserLocationPaint);
            canvas.drawText("x: " + x + ", y: " + y, x + 50, y + 5, mLabelPaint);

            mExistingUserLocation = userLocation;
        }
    }

    private Location filteredLocation(Location newLocation, Location previousLocation) {
        double x = previousLocation.x * (1 - IBeaconConstants.BLEND_FACTOR) + newLocation.x * IBeaconConstants.BLEND_FACTOR;
        double y = previousLocation.y * (1 - IBeaconConstants.BLEND_FACTOR) + newLocation.y * IBeaconConstants.BLEND_FACTOR;

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
