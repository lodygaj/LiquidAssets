package com.liquidassets.liquidassetsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Byte4: Liquid Assets on 3/19/2016.
 */
public class ListUserAdapter extends BaseAdapter {
    ArrayList<User> users;
    private Context context;
    private LayoutInflater inflater;

    public ListUserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_list_item, null);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        User currentUser = users.get(position);
        txtDescription.setText(currentUser.getUsername().toString());
        return view;
    }
}
