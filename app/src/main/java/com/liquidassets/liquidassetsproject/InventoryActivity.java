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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Byte4: Liquid Assets on 2/1/2016.
 */

public class InventoryActivity extends AppCompatActivity implements TextWatcher {
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayAdapter<Product> searchAdapter;
    private ArrayList<Product> products;
    private AutoCompleteTextView autoSearch;
    private Button logout;
    private DBHelper dbHelper;
    private ListView productList;
    private ListProductAdapter listAdapter;
    private Product selectedProduct;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Create objects from layout
        spinner = (Spinner) findViewById(R.id.spinner);
        autoSearch = (AutoCompleteTextView)findViewById(R.id.autoSearch);
        productList = (ListView) findViewById(R.id.productList);
        logout = (Button) findViewById(R.id.btnLogout);

        // Create database helper object
        dbHelper = new DBHelper(this);

        // Get list of all products in database
        products = dbHelper.getProducts();

        // Set up auto complete text view search bar
        autoSearch.addTextChangedListener(this);
        // Create auto complete search adapter
        searchAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_dropdown_item_1line, products);
        // Attach adapter to auto search
        autoSearch.setAdapter(searchAdapter);
        autoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                // Show product detail in fragment
                Product myProduct = (Product) adapterView.getAdapter().getItem(position);
                InventoryDetailFragment detailFragment = new InventoryDetailFragment();
                setFragment(detailFragment);
                detailFragment.change(myProduct.getDescription(),
                        Integer.toString(myProduct.getSku()),
                        Double.toString(myProduct.getPrice()),
                        Integer.toString(myProduct.getQuantity()),
                        myProduct.getCategory());
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
        // Attach adapter to spinner
        spinner.setAdapter(spinnerAdapter);
        // Set initial selection to null
        spinner.setSelection(0, false);
        // Listener to determine what happens when item is selected from drop down
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Show selected category in list view
                String categorySelected = spinner.getSelectedItem().toString();
                if(!categorySelected.equals("All Products")) {
                    // Update list to show products by category
                    products = dbHelper.getProductsByCategory(categorySelected);
                    listAdapter.products = products;
                    listAdapter.notifyDataSetChanged();
                }
                else {
                    // Update list to show all products
                    products = dbHelper.getProducts();
                    listAdapter.products = products;
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        // Create product list view adapter
        listAdapter = new ListProductAdapter(this, products);
        // Attach adapter to list view
        productList.setAdapter(listAdapter);
        // Listener to determine what happens when item is selected from list
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected product
                selectedProduct = products.get(position);
                // Show product detail in fragment
                InventoryDetailFragment detailFragment = new InventoryDetailFragment();
                setFragment(detailFragment);
                detailFragment.change(selectedProduct.getDescription(),
                        Integer.toString(selectedProduct.getSku()),
                        Double.toString(selectedProduct.getPrice()),
                        Integer.toString(selectedProduct.getQuantity()),
                        selectedProduct.getCategory());
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

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }

    // List Product Adapter Getter
    public ListProductAdapter getListAdapter() {
        return listAdapter;
    }

    // List Product Adapter Setter
    public void setListAdapter(ListProductAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    // Search Adapter Getter
    public ArrayAdapter<Product> getSearchAdapter() {
        return searchAdapter;
    }

    // Search Adapter Setter
    public void setSearchAdapter(ArrayAdapter<Product> searchAdapter) {
        this.searchAdapter = searchAdapter;
    }

    // Method to insert sample products for testing database only
    public void addSampleProducts() {
        ArrayList<Product> addProductsList = new ArrayList<Product>();

        //freshwater fish
        Product p = new Product("Betta", 256342, 7.99, 56, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Goldfish", 357986, 12.99, 12, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Amazon Puffer", 357986, 19.99,15, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Emerald Catfish", 2468756, 25.99, 7, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Reedfish", 259876, 9.99, 22, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Black Phantom Tetra", 956784, 9.99, 5, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Red Piranha", 635784, 13.99, 17, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Beckford Pencilfish", 968521, 7.99, 9, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Rusty Cichlid", 795248, 29.99, 10, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Red Zebra Cichlid", 324877, 17.99, 3, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Zebra Tilapia", 124703, 12.99, 9, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Spotted Angelfish", 365104, 13.99, 12, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Clown Barb", 269665, 6.99, 20, "Freshwater Fish");
        addProductsList.add(p);
        p = new Product("Spotted Danio", 487998, 25.99, 1, "Freshwater Fish");
        addProductsList.add(p);

        // saltwater fish
        p = new Product("Snowflake Eeel", 657489, 21.99, 3, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Yellow Tang", 325419, 37.99, 7, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Blue Hippo Tang", 587469, 29.99, 21, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Black Benny", 321987, 22.99, 1, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Yellow Clown Goby", 649852, 10.99, 2, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Copperband Butterflyfish", 102478, 34.99, 5, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Racoon Butterflyfish", 982013, 34.99, 7, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Yellowfin Angelfish", 302145, 49.99, 9, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Engineer Goby", 601247, 9.99, 10, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Powder Blue Tang", 620321, 64.99, 3, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Bicolor Angel", 902106, 34.99, 19, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Pink Skunk Clownfish", 800745, 19.99, 8, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Rusty Angel", 420654, 29.99, 20, "Saltwater Fish");
        addProductsList.add(p);
        p = new Product("Blue Fin Angel", 478963, 25.99, 1, "Saltwater Fish");
        addProductsList.add(p);

        // dry goods
        p = new Product("Filter", 982624, 7.99, 3, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Light", 632478, 12.99, 7, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Fish Food", 650974, 19.99, 21, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Heater", 302956, 25.99, 1, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Water Test Kit", 365089, 9.99, 2, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Feeder", 456290, 9.99, 5, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Water Pump", 624620, 13.99, 7, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Sterilizer", 659660, 7.99, 9, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Substrate", 789454, 29.99, 10, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Timer", 620142, 17.99, 3, "Dry Goods");
        addProductsList.add(p);
        p = new Product("Cleaner", 635940, 12.99, 19, "Dry Goods");
        addProductsList.add(p);

        for(Product prod: addProductsList) {
            dbHelper.addProduct(prod);
        }
    }
}
