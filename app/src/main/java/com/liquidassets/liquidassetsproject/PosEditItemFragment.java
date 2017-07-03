package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Byte4: Liquid Assets on 3/22/2016.
 */
public class PosEditItemFragment extends Fragment {
    private ArrayList<SaleItem> saleItems;
    private Boolean isDiscounted;
    private Button submit, cancel;
    private DBHelper dbHelper;
    private EditText discount, price, quantity;
    private TextView description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pos_edit_item, container, false);

        // Create objects from layout
        description = (TextView) view.findViewById(R.id.txtDescriptionDisplay);
        price = (EditText) view.findViewById(R.id.edtTxtPrice);
        quantity = (EditText) view.findViewById(R.id.edtTxtQuantity);
        discount = (EditText) view.findViewById(R.id.edtTxtPercentOff);
        submit = (Button) view.findViewById(R.id.btnSubmit);
        cancel = (Button) view.findViewById(R.id.btnCancel);

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // Get arraylist of items in sale list
        saleItems = getItems();

        // Listener handles when Submit button is pressed
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get item details
                String newDescription = description.getText().toString();
                double newPrice = Double.parseDouble(price.getText().toString());
                int newQuantity = Integer.parseInt(quantity.getText().toString());
                int newDiscount = Integer.parseInt(discount.getText().toString());

                // Update array list with new info
                for(SaleItem item: saleItems) {
                    if(item.getDescription().equals(newDescription)) {
                        // If item was not originally discounted
                        if(!isDiscounted) {
                            // Item is still not discounted
                            if(newDiscount == 0) {
                                // Update item values
                                item.setPrice(newPrice);
                                item.setQuantity(newQuantity);
                                item.setExtPrice(newPrice * newQuantity);
                            }
                            // Item is now discounted
                            else {
                                // calculate discounted price
                                double discountedPrice = newPrice * (1 - ((double) newDiscount / 100));
                                // Round to 2 decimal places
                                discountedPrice = Math.round(discountedPrice * 100.0) / 100.0;
                                // Update item values
                                item.setPrice(discountedPrice);
                                item.setQuantity(newQuantity);
                                item.setExtPrice(discountedPrice * newQuantity);
                                item.setPercentOff(newDiscount);
                                item.setDiscount(true);
                            }
                        }
                        // If item was originally discounted
                        else if(isDiscounted) {
                            // Item is no longer discounted
                            if(newDiscount == 0) {
                                // Get original price from database
                                double originalPrice = dbHelper.getProduct(newDescription).getPrice();
                                // Update item values
                                item.setPrice(originalPrice);
                                item.setQuantity(newQuantity);
                                item.setExtPrice(originalPrice * newQuantity);
                                item.setPercentOff(newDiscount);
                                item.setDiscount(false);
                            }
                            // Item is still discounted
                            else {
                                // Get original price from database
                                double originalPrice = dbHelper.getProduct(newDescription).getPrice();
                                double discountedPrice = originalPrice * (1 - ((double) newDiscount / 100));
                                // Round to 2 decimal places
                                discountedPrice = Math.round(discountedPrice * 100.0) / 100.0;
                                // Update item values
                                item.setPrice(discountedPrice);
                                item.setQuantity(newQuantity);
                                item.setExtPrice(discountedPrice * newQuantity);
                                item.setPercentOff(newDiscount);
                            }
                        }
                    }
                }

                // Set updated arraylist of sale items
                setItems(saleItems);

                // Switch fragment back to sale window
                PosSaleFragment fragment = new PosSaleFragment();
                setFragment(fragment);

                // Update totals in sale fragment
                double tempTotal = 0;
                for(SaleItem item: saleItems) {
                    tempTotal += item.getExtPrice();
                }

                // Format numbers into currency strings to display on screen
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String subtotal = formatter.format(tempTotal);
                String tax = formatter.format(tempTotal * .06);
                String total = formatter.format(tempTotal * 1.06);
                fragment.updateTotals(subtotal, tax, total);
            }
        });

        // Listener handles when Cancel button is pressed
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to sale fragment
                PosSaleFragment fragment = new PosSaleFragment();
                setFragment(fragment);

                // Update totals in sale fragment
                double tempTotal = 0;
                for(SaleItem item: saleItems) {
                    tempTotal += item.getExtPrice();
                }
                // Format numbers into currency strings to display on screen
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String subtotal = formatter.format(tempTotal);
                String tax = formatter.format(tempTotal * .06);
                String total = formatter.format(tempTotal * 1.06);
                fragment.updateTotals(subtotal, tax, total);
            }
        });

        return view;
    }

    // Sets sale item details in fields
    public void setFields(String txt, String txt1, String txt2, String txt3) {
        description.setText(txt);
        price.setText(txt1);
        quantity.setText(txt2);
        discount.setText(txt3);
    }

    // Get array of products to display in list view
    private ArrayList<SaleItem> getItems() {
        ArrayList<SaleItem> products = new ArrayList<SaleItem>();
        products = ((POSActivity)getActivity()).getSaleList();
        return products;
    }

    // Set array of products to display in list view
    private void setItems(ArrayList<SaleItem> products) {
        ((POSActivity)getActivity()).setSaleList(products);
    }

    // isDiscounted setter
    public void setIsDiscounted(Boolean isDiscounted) {
        this.isDiscounted = isDiscounted;
    }

    // Method called to upgrade fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
