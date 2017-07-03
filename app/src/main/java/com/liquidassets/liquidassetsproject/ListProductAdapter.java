package com.liquidassets.liquidassetsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Byte4: Liquid Assets on 2/26/2016.
 */
public class ListProductAdapter extends BaseAdapter {
    ArrayList<Product> products;
    private Context context;
    private LayoutInflater inflater;

    public ListProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_list_item, null);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        Product currentProduct = products.get(position);
        txtDescription.setText(currentProduct.getDescription().toString());
        return view;
    }
}
