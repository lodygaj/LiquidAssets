package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Byte4: Liquid Assets on 3/17/2016.
 */
public class InventoryCreateItemFragment extends Fragment {
    private Button submit, cancel;
    private DBHelper dbHelper;
    private double newPrice;
    private EditText description, price, quantity, sku;
    private int newSku, newQuantity;
    private Spinner spinner;
    private String newDesc, newCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_create_item, container, false);

        // Create objects
        description = (EditText) view.findViewById(R.id.txtDescriptionDisplay);
        price = (EditText) view.findViewById(R.id.edtTxtPrice);
        quantity = (EditText) view.findViewById(R.id.edtTxtQuantity);
        sku = (EditText) view.findViewById(R.id.txtSkuDisplay);
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
        // Listener to determine what happens when item is selected from drop down
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Not Used
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Not Used
            }
        });

        // Listener handles when Submit button is pressed
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from fields
                newDesc = description.getText().toString();
                newSku = Integer.parseInt(sku.getText().toString());
                newPrice = Double.parseDouble(price.getText().toString());
                newQuantity = Integer.parseInt(quantity.getText().toString());
                newCategory = spinner.getSelectedItem().toString();

                // Create new product object from fields
                Product newProduct = new Product(newDesc, newSku, newPrice, newQuantity, newCategory );

                // Check if description is 40 characters or less
                if(newDesc.length() <= 25) {
                    // Check if product already exists
                    if(!dbHelper.checkIfProductExists(newProduct)) {
                        // Submit new item to database
                        dbHelper.addProduct(newProduct);
                        Toast.makeText(getActivity().getApplicationContext(), "Item created successfuly!",
                                Toast.LENGTH_SHORT).show();

                        // Show new item in detail fragment
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

                        // Display updated list
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
                        Toast.makeText(getActivity().getApplicationContext(), "Item already in database!",
                                Toast.LENGTH_SHORT).show();
                    }
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
            BlankFragment fragment = new BlankFragment();
            setFragment(fragment);
            }
        });

        return view;
    }

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
