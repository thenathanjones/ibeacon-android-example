package com.thenathanjones.ibeacon;

import android.app.Activity;
import android.os.Bundle;

import com.thenathanjones.ibeaconlibrary.R;

import java.util.Collection;

public class CanvasActivity extends Activity implements IBeaconListener {

    private IBeaconService mIBeaconService;
    private LocationView mLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        mIBeaconService = new IBeaconService(this);

        mLocationView = (LocationView) findViewById(R.id.locationView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIBeaconService.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIBeaconService.registerListener(this);
    }

    @Override
    public void beaconsFound(final Collection<IBeacon> beacons) {
        mLocationView.updateBeaconsWith(beacons);
    }
}
