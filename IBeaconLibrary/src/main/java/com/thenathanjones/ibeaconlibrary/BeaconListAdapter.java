package com.thenathanjones.ibeaconlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thenathanjones.ibeaconlibrary.services.IBeacon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by thenathanjones on 31/01/2014.
 */
public class BeaconListAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;

    private List<IBeacon> mBeacons = new ArrayList<IBeacon>();

    public BeaconListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mBeacons.size();
    }

    public Object getItem(int position) {
        return mBeacons.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public void updateBeacons(Collection<IBeacon> newBeacons) {
        mBeacons.clear();
        mBeacons.addAll(newBeacons);
    }

    public View getView(int position, View itemView, ViewGroup parent) {
        ViewHolder holder;
        IBeacon corresponding = mBeacons.get(position);

        if (itemView == null) {
            itemView = mInflater.inflate(R.layout.list_beacon, parent, false);

            holder = new ViewHolder();
            holder.uuid = (TextView)itemView.findViewById(R.id.uuid);
            holder.major = (TextView)itemView.findViewById(R.id.major);
            holder.minor = (TextView)itemView.findViewById(R.id.minor);
            holder.txPower = (TextView)itemView.findViewById(R.id.txPower);
            holder.distance = (TextView)itemView.findViewById(R.id.distance);

            itemView.setTag(holder);
        }
        else {
            holder = (ViewHolder)itemView.getTag();
        }

        holder.uuid.setText("UUID: " + corresponding.uuid);
        holder.major.setText("Minor: " + corresponding.major);
        holder.minor.setText("Minor: " + corresponding.minor);
        holder.txPower.setText("Tx Power: " + corresponding.txPower);
        DecimalFormat df = new DecimalFormat("#.##");
        holder.distance.setText("Approx. distance: " + df.format(corresponding.accuracyInMetres) + "m");

        return itemView;
    }

    private static class ViewHolder {
        public TextView uuid;
        public TextView major;
        public TextView minor;
        public TextView txPower;
        public TextView distance;
    }
}
