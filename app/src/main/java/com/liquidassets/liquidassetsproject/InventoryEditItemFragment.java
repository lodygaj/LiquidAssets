package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Byte4: Liquid Assets on 3/17/2016.
 */
public class InventoryEditItemFragment extends Fragment {
    private Button submit, cancel;
    private DBHelper dbHelper;
    private EditText description, price, quantity;
    private Spinner spinner;
    private String oldDesc, oldSku, oldPrice, oldQuantity, oldCategory;
    private TextView sku;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_edit_item, container, false);

        // Create objects
        sku = (TextView)view.findViewById(R.id.txtSkuDisplay);
        description = (EditText) view.findViewById(R.id.txtDescriptionDisplay);
        price = (EditText) view.findViewById(R.id.edtTxtPrice);
        quantity = (EditText) view.findViewById(R.id.edtTxtQuantity);
        submit = (Button) view.findViewById(R.id.btnSubmit);
        cancel = (Button) view.findViewById(R.id.btnCancel);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // Create array of spinner elements
        List<String> categories = new ArrayList<String>();
        categories.add("Dry Goods");
        categories.add("Freshwater Fish");
        categories.add("Saltwater Fish");
        categories.add("Water");

        // Create spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, categories);
        // Set drop down layout style for spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attach adapter to spinner
        spinner.setAdapter(adapter);
        // Listener handles when Submit button is pressed
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if description is less than 25 characters
                if(description.getText().toString().length() <= 25) {
                    // Submit changes to database
                    Product editedProduct = new Product(description.getText().toString(),
                            Integer.parseInt(sku.getText().toString()),
                            Double.parseDouble(price.getText().toString()),
                            Integer.parseInt(quantity.getText().toString()),
                            spinner.getSelectedItem().toString());
                    dbHelper.editProduct(editedProduct, oldDesc);

                    Toast.makeText(getActivity().getApplicationContext(), "Item edited successfuly!",
                            Toast.LENGTH_SHORT).show();

                    // Switch to detail fragment
                    InventoryDetailFragment fragment = new InventoryDetailFragment();
                    setFragment(fragment);
                    fragment.change(description.getText().toString(),
                            sku.getText().toString(),
                            price.getText().toString(),
                            quantity.getText().toString(),
                            spinner.getSelectedItem().toString());

                    // Update list view to show new item
                    ArrayList<Product> newProducts = dbHelper.getProducts();
                    ListProductAdapter newListAdapter = ((InventoryActivity) getActivity()).getListAdapter();
                    newListAdapter.products = newProducts;
                    ((InventoryActivity) getActivity()).setListAdapter(newListAdapter);
                    ((InventoryActivity) getActivity()).getListAdapter().notifyDataSetChanged();

                    // Update search bar to find new item
                    ArrayAdapter<Product> newSearchAdapter = ((InventoryActivity) getActivity()).getSearchAdapter();
                    newSearchAdapter.clear();
                    newSearchAdapter.addAll(newProducts);
                    ((InventoryActivity) getActivity()).setSearchAdapter(newSearchAdapter);
                    ((InventoryActivity) getActivity()).getSearchAdapter().notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Description must be less than 25 charcters!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener handles when Cancel button is pressed
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryDetailFragment fragment = new InventoryDetailFragment();
                setFragment(fragment);
                fragment.change(oldDesc, oldSku, oldPrice, oldQuantity, oldCategory);
            }
        });

        return view;
    }

    // This method changes what is displayed in detail window when item is chosen from list
    public void change(String txt, String txt1, String txt2, String txt3, String txt4, int pos){
        // Display values for product
        description.setText(txt);
        sku.setText(txt1);
        price.setText(txt2);
        quantity.setText(txt3);
        spinner.setSelection(pos);

        // Set old values for product to display in detail fragment when edit is cancelled
        oldDesc = txt;
        oldSku = txt1;
        oldPrice = txt2;
        oldQuantity = txt3;
        oldCategory = txt4;
    }

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
