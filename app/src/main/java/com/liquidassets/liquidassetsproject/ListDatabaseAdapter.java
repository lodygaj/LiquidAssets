package com.liquidassets.liquidassetsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Byte4: Liquid Assets on 4/17/2016.
 */
public class ListDatabaseAdapter extends BaseAdapter {
    ArrayList<File> files;
    private Context context;
    private LayoutInflater inflater;

    public ListDatabaseAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_list_item, null);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        File currentFile = files.get(position);
        txtDescription.setText(currentFile.getName());
        return view;
    }
}
