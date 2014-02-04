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
    private int mCanvasWidth;
    private int mCanvasHeight;
    private final List<IBeacon> mBeacons = new ArrayList<IBeacon>();
    private final Map<String, IBeaconLocation> mBeaconLocations = new HashMap<String, IBeaconLocation>() {{
        put("55741", IBeaconLocation.from(0.25, 0.25, 1));
        put("240", IBeaconLocation.from(0.35, 0.35, 1));
        put("268", IBeaconLocation.from(0.1, 0.2, 0.2));
        put("27760", IBeaconLocation.from(1, 1, 1));
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

                    drawBeaconsOn(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawBeaconsOn(Canvas canvas) {
        for (IBeacon report : mBeacons) {
            IBeaconLocation beaconLocation = mBeaconLocations.get(report.minor + "");
            if (beaconLocation != null) {
                canvas.drawCircle((int)(beaconLocation.x * mCanvasHeight), (int)(beaconLocation.y * mCanvasWidth), 5, mLocationPaint);
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
