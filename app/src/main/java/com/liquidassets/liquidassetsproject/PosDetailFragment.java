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
 * Created by Byte4: Liquid Assets on 2/21/2016.
 */
public class PosDetailFragment extends Fragment {
    private Button addToCart, backToSale;
    private EditText addQuantity, percentOff;
    private SaleItem currentProduct;
    private TextView description, sku, price, quantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pos_detail, container, false);

        // Create objects
        description = (TextView) view.findViewById(R.id.txtDescriptionDisplay);
        sku = (TextView) view.findViewById(R.id.txtSkuDisplay);
        price = (TextView )view.findViewById(R.id.txtPriceDisplay);
        quantity = (TextView) view.findViewById(R.id.txtQuantityDisplay);
        addQuantity = (EditText) view.findViewById(R.id.edtTxtAddQuantity);
        percentOff = (EditText) view.findViewById(R.id.edtTxtPercentOff);
        addToCart = (Button) view.findViewById(R.id.btnSubmit);
        backToSale = (Button) view.findViewById(R.id.btnBackToSale);

        // Listener to handle what happens when Add To Sale button is clicked
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get item values from fragment
                String itemDescription = description.getText().toString();
                int itemSku = Integer.parseInt(sku.getText().toString());
                double itemPrice = Double.parseDouble(price.getText().toString());
                int itemQuantity = Integer.parseInt(addQuantity.getText().toString());
                double extPrice = itemPrice * itemQuantity;
                int itemPercent = 0;
                boolean discount = false;

                // If percent off is used
                if(!percentOff.getText().toString().equals("")) {
                    // Calculate price
                    itemPercent = Integer.parseInt(percentOff.getText().toString());
                    itemPrice = itemPrice * (1 - ((double) itemPercent / 100));
                    // Round to 2 decimal places
                    itemPrice = Math.round(itemPrice * 100.0) / 100.0;
                    extPrice = itemPrice * itemQuantity;
                    discount = true;
                }

                // Create product object for display in pos sale list
                SaleItem p = new SaleItem(itemDescription, itemSku, itemPrice, extPrice, itemQuantity, itemPercent, discount);

                // Get current array list of sale items
                ArrayList<SaleItem> temp = ((POSActivity)getActivity()).getSaleList();

                // Determine if sku has already been added to sale
                boolean alreadyExists = false;
                for(SaleItem item: temp) {
                    if(item.getDescription().equals(p.getDescription())) {
                        alreadyExists = true;
                        currentProduct = item;
                    }
                }

                // If item already exists in sale list
                if(alreadyExists) {
                    // Existing item and new item are discounted
                    if(currentProduct.isDiscount() && p.isDiscount()) {
                        // Update quantity of item
                        currentProduct.setQuantity(currentProduct.getQuantity() + p.getQuantity());
                    }
                    // Existing item and new item are not discounted
                    else if(!currentProduct.isDiscount() && !p.isDiscount()) {
                        // Update quantity of item
                        currentProduct.setQuantity(currentProduct.getQuantity() + p.getQuantity());
                    }
                    // Existing product is not discounted and new item is discounted
                    else if(!currentProduct.isDiscount() && p.isDiscount()) {
                        // Add new item to sale list
                        temp.add(p);
                    }
                    // Existing product is discounted and new item is not discounted
                    else if (currentProduct.isDiscount() && !p.isDiscount()) {
                        // Add new item to sale list
                        temp.add(p);
                    }
                }
                // Item does not already exist in sale list
                else {
                    // Add new item to sale list
                    temp.add(p);
                }

                // Set updated sale item list
                ((POSActivity)getActivity()).setSaleList(temp);

                // Switch to sale fragment
                PosSaleFragment saleFragment = new PosSaleFragment();
                setFragment(saleFragment);

                // Update totals in sale fragment
                double tempTotal = 0;
                for(SaleItem item: temp) {
                    tempTotal += item.getExtPrice();
                }
                // Format numbers into currency strings to display on screen
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String subtotal = formatter.format(tempTotal);
                String tax = formatter.format(tempTotal * .06);
                String total = formatter.format(tempTotal * 1.06);
                saleFragment.updateTotals(subtotal, tax, total);
            }
        });

        // Listener to handle what happens when Back To Sale button is clicked
        backToSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get array list of sale items
                ArrayList<SaleItem> temp = ((POSActivity)getActivity()).getSaleList();

                // Switch fragment to sale window
                PosSaleFragment saleFragment = new PosSaleFragment();
                setFragment(saleFragment);

                // Update totals in sale fragment
                double tempTotal = 0;
                for(SaleItem product: temp) {
                    tempTotal += product.getExtPrice();
                }
                // Format numbers into currency strings to display on screen
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String subtotal = formatter.format(tempTotal);
                String tax = formatter.format(tempTotal * .06);
                String total = formatter.format(tempTotal * 1.06);
                saleFragment.updateTotals(subtotal, tax, total);
            }
        });

        return view;
    }

    // This method changes what is displayed in detail window when item is chosen from list
    public void setDetails(String txt, String txt1, String txt2, String txt3) {
        description.setText(txt);
        sku.setText(txt1);
        price.setText(txt2);
        quantity.setText(txt3);
    }

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
