package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Byte4: Liquid Assets on 2/1/2016.
 */
public class POSActivity extends AppCompatActivity implements TextWatcher {
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<Product> products;
    private ArrayList<SaleItem> saleList;
    private AutoCompleteTextView autoSearch;
    private Button logout;
    private DBHelper dbHelper;
    private ListView productList;
    private ListProductAdapter listAdapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        // Create objects
        spinner = (Spinner) findViewById(R.id.spinner);
        productList = (ListView) findViewById(R.id.productList);
        autoSearch = (AutoCompleteTextView)findViewById(R.id.autoSearch);
        logout = (Button) findViewById(R.id.btnLogout);
        saleList = new ArrayList<SaleItem>();

        // Create database helper object
        dbHelper = new DBHelper(this);

        // Get list of all products in database
        products = dbHelper.getProducts();

        // Set up auto complete text view search bar
        autoSearch.addTextChangedListener(this);
        autoSearch.setAdapter(new ArrayAdapter<Product>(this, android.R.layout.simple_dropdown_item_1line, products));
        autoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                // Show product detail in fragment
                Product myProduct = (Product) adapterView.getAdapter().getItem(position);
                PosDetailFragment detailFragment = new PosDetailFragment();
                setFragment(detailFragment);
                detailFragment.setDetails(myProduct.getDescription(),
                        Integer.toString(myProduct.getSku()),
                        Double.toString(myProduct.getPrice()),
                        Integer.toString(myProduct.getQuantity()));
                // Clear search field
                autoSearch.setText("");
            }
        });

        // Create array of spinner elements
        List<String> categories = new ArrayList<String>();
        categories.add("All Products");
        categories.add("Dry Goods");
        categories.add("Freshwater Fish");
        categories.add("Saltwater Fish");
        categories.add("Water");

        // Create spinner adapter
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Set drop down layout style for spinner
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attach data adapter to spinner
        spinner.setAdapter(spinnerAdapter);
        // Set initial selection
        spinner.setSelection(0, false);
        // Listener to determine what happens when item is selected from drop down
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected category
                String categorySelected = spinner.getSelectedItem().toString();
                // Show selected category in list view
                if(!categorySelected.equals("All Products")) {
                    products = dbHelper.getProductsByCategory(categorySelected);
                    listAdapter.products = products;
                    listAdapter.notifyDataSetChanged();
                }
                else {
                    products = dbHelper.getProducts();
                    listAdapter.products = products;
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Not Used
            }
        });

        // Create product list view adapter
        listAdapter = new ListProductAdapter(this, products);
        // Attach adapter to list view
        productList.setAdapter(listAdapter);
        // Listener to determine what hapens when item is selected from list
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show product detail in fragment
                PosDetailFragment detailFragment = new PosDetailFragment();
                setFragment(detailFragment);
                detailFragment.setDetails(listAdapter.products.get(position).getDescription(),
                        Integer.toString(listAdapter.products.get(position).getSku()),
                        Double.toString(listAdapter.products.get(position).getPrice()),
                        Integer.toString(listAdapter.products.get(position).getQuantity()));
            }
        });

        // Listener to handle what happens when Logout button is clicked
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutStartIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logoutStartIntent);
            }
        });
    }

    // The following methods handle the auto complete text view search bar
    @Override
    public void afterTextChanged(Editable arg0) { }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    // Method called to upgrade fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }

    // Sale list Getter for PosSaleFragment
    public ArrayList<SaleItem> getSaleList() {
        return saleList;
    }

    // Sale list Setter for PosSaleFragment
    public void setSaleList(ArrayList<SaleItem> saleList) {
        this.saleList = saleList;
    }

    // List Product Adapter Getter
    public ListProductAdapter getListAdapter() {
        return listAdapter;
    }

    // List Product Adapter Setter
    public void setListAdapter(ListProductAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }
}
