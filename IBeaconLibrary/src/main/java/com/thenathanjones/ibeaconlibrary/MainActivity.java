package com.thenathanjones.ibeaconlibrary;

import android.app.Activity;
import android.os.Bundle;

import com.thenathanjones.ibeaconlibrary.services.IBeaconListener;
import com.thenathanjones.ibeaconlibrary.services.IBeaconService;

public class MainActivity extends Activity implements IBeaconListener {

    private IBeaconService mIBeaconService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIBeaconService = new IBeaconService(this);
        mIBeaconService.registerListener(this);
    }
}
