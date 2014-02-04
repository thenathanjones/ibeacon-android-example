package com.thenathanjones.ibeaconlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thenathanjones.ibeaconlibrary.services.IBeacon;

import java.util.ArrayList;
import java.util.Collection;
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
    private double mRoomWidth = 727;
    private double mRoomHeight = 771;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private final List<IBeacon> mBeacons = new ArrayList<IBeacon>();
    private final Map<String, IBeaconLocation> mBeaconLocations = new HashMap<String, IBeaconLocation>() {{
        put("55741", IBeaconLocation.from(597, 30, 1));
        put("240", IBeaconLocation.from(487, 641, 1));
        put("268", IBeaconLocation.from(230, 60, 0.2));
        put("27760", IBeaconLocation.from(42, 270, 1));
        put("30397", IBeaconLocation.from(1, 1, 1));
    }};

    private Paint mLocationPaint;

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
                int x = (int) ((beaconLocation.x / mRoomWidth) * mCanvasWidth);
                int y = (int) ((beaconLocation.y / mRoomHeight) * mCanvasHeight);
                canvas.drawCircle(x, y, 5, mLocationPaint);
            }
        }
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
