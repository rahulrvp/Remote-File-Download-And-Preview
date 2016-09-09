package com.github.rahulrvp.getdriveurl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rahul on 9/9/16.
 */
public class DriveListAdapter extends BaseAdapter {

    ArrayList<DriveItem> mItems = new ArrayList<>();

    public void addItem(DriveItem item) {
        mItems.add(item);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public DriveItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drive_list_item, parent, false);
        }

        DriveItem driveItem = getItem(position);
        if (driveItem != null) {
            TextView name = (TextView) convertView.findViewById(R.id.item_name);
            name.setText(driveItem.getName());
        }

        return convertView;
    }
}
