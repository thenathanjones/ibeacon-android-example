package com.thenathanjones.ibeaconlibrary;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.thenathanjones.ibeaconlibrary.services.IBeacon;
import com.thenathanjones.ibeaconlibrary.services.IBeaconListener;
import com.thenathanjones.ibeaconlibrary.services.IBeaconService;

import java.util.Collection;

public class MainActivity extends Activity implements IBeaconListener {

    private IBeaconService mIBeaconService;
    private ListView mListView;
    private BeaconListAdapter mListAdapter;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIBeaconService = new IBeaconService(this);


        mListView = (ListView)findViewById(R.id.beaconList);
        mListAdapter = new BeaconListAdapter(this);
        mListView.setAdapter(mListAdapter);
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListAdapter.updateBeacons(beacons);
                mListAdapter.notifyDataSetChanged();
            }
        });
    }
}
