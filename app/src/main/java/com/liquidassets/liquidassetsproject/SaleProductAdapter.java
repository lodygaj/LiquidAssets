package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Byte4: Liquid Assets on 2/23/2016.
 */
public class SaleProductAdapter extends BaseAdapter {
    private ArrayList<SaleItem> products;
    private Context context;
    private LayoutInflater inflater;

    public SaleProductAdapter(Context context, ArrayList<SaleItem> products) {
        this.context = context;
        this.products = products;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(products == null) {
            return 0;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_product_item, null);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        TextView txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
        TextView txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        TextView txtExtPrice = (TextView) view.findViewById(R.id.txtExtPrice);
        Button btnRemove = (Button) view.findViewById(R.id.btnRemove);

        SaleItem currentProduct = products.get(position);

        if(currentProduct.isDiscount()) {
            txtDescription.setText(currentProduct.getDescription() + "  -" + currentProduct.getPercentOff() + "%");
        }
        else {
            txtDescription.setText(currentProduct.getDescription());
        }
        txtQuantity.setText(Integer.toString(currentProduct.getQuantity()));
        txtPrice.setText(Double.toString(currentProduct.getPrice()));
        txtExtPrice.setText(Double.toString(currentProduct.getExtPrice()));

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove Sale Item object from Sale List
                ArrayList<SaleItem> products = ((POSActivity)context).getSaleList();
                products.remove(position);

                // Refresh sale fragment
                PosSaleFragment saleFragment = new PosSaleFragment();
                setFragment(saleFragment);

                // Update totals in sale fragment
                double tempTotal = 0;
                for(SaleItem item: products) {
                    tempTotal += item.getExtPrice();
                }
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String subtotal = formatter.format(tempTotal);
                String tax = formatter.format(tempTotal * .06);
                String total = formatter.format(tempTotal * 1.06);
                saleFragment.updateTotals(subtotal, tax, total);
            }
        });

        return view;
    }

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = ((POSActivity)context).getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
