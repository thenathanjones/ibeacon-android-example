package com.thenathanjones.ibeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.thenathanjones.ibeaconlibrary.R;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.canvas:
                goToCanvas();
                break;
        }

        return false;
    }

    private void goToCanvas() {
        Intent viewCanvas = new Intent(this, CanvasActivity.class);
        startActivity(viewCanvas);
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
