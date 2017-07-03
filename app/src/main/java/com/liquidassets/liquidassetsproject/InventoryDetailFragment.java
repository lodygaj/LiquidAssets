package com.liquidassets.liquidassetsproject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Byte4: Liquid Assets on 2/18/2016.
 */
public class InventoryDetailFragment extends Fragment {
    private Button addQuantity, removeQuantity, editItem, createItem, deleteItem;
    private DBHelper dbHelper;
    private EditText adjustQuantity;
    private TextView description, sku, price, quantity, category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_detail, container, false);

        // Create objects
        description = (TextView) view.findViewById(R.id.txtDescriptionDisplay);
        sku = (TextView)view.findViewById(R.id.txtSkuDisplay);
        price = (TextView)view.findViewById(R.id.txtPriceDisplay);
        quantity = (TextView)view.findViewById(R.id.txtQuantityDisplay);
        category = (TextView)view.findViewById(R.id.txtCategoryDisplay);
        adjustQuantity = (EditText)view.findViewById(R.id.edtTxtAdjustQuantity);
        addQuantity = (Button) view.findViewById(R.id.btnSubmit);
        removeQuantity = (Button) view.findViewById(R.id.btnRemove);
        editItem = (Button) view.findViewById(R.id.btnCancel);
        createItem = (Button) view.findViewById(R.id.btnCreateItem);
        deleteItem = (Button) view.findViewById(R.id.btnDeleteItem);

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // Listener handles when Add Quantity button is pressed
        addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get new quantity
                int adjust = Integer.parseInt(adjustQuantity.getText().toString());
                int oldQuantity = Integer.parseInt(quantity.getText().toString());
                int newQuantity = oldQuantity + adjust;

                // Create product object to replace product in database
                String newDescription = description.getText().toString();
                int newSku = Integer.parseInt(sku.getText().toString());
                double newPrice = Double.parseDouble(price.getText().toString());
                String newCategory = category.getText().toString();

                Product p = new Product(newDescription, newSku, newPrice, newQuantity, newCategory);

                // Update database
                try {
                    dbHelper.editProduct(p, newDescription);
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Added " + adjust + " units to inventory!",
                            Toast.LENGTH_SHORT).show();

                    // Set new quantity in detail window
                    quantity.setText(Integer.toString(newQuantity));
                    // Clear adjust quantity field
                    adjustQuantity.setText("");

                    // Refresh list view to update product data
                    ArrayList<Product> newProducts = dbHelper.getProducts();
                    ListProductAdapter newListAdapter = ((InventoryActivity) getActivity()).getListAdapter();
                    newListAdapter.products = newProducts;
                    ((InventoryActivity) getActivity()).setListAdapter(newListAdapter);
                    ((InventoryActivity) getActivity()).getListAdapter().notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Database error! Unable to update quantity!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener handles when Remove Quantity button is pressed
        removeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get new quantity
                int adjust = Integer.parseInt(adjustQuantity.getText().toString());
                int oldQuantity = Integer.parseInt(quantity.getText().toString());
                int newQuantity = oldQuantity - adjust;

                // Create product object to replace product in database
                String newDescription = description.getText().toString();
                int newSku = Integer.parseInt(sku.getText().toString());
                double newPrice = Double.parseDouble(price.getText().toString());
                String newCategory = category.getText().toString();

                Product p = new Product(newDescription, newSku, newPrice, newQuantity, newCategory);

                // Update database
                try {
                    dbHelper.editProduct(p, newDescription);
                    Toast.makeText(getActivity().getApplicationContext(), "Removed " + adjust +
                            " units from inventory!", Toast.LENGTH_SHORT).show();

                    // Set new quantity in detail window
                    quantity.setText(Integer.toString(newQuantity));
                    // Clear adjust quantity field
                    adjustQuantity.setText("");

                    // Refresh list view to update product data
                    ArrayList<Product> newProducts = dbHelper.getProducts();
                    ListProductAdapter newListAdapter = ((InventoryActivity) getActivity()).getListAdapter();
                    newListAdapter.products = newProducts;
                    ((InventoryActivity) getActivity()).setListAdapter(newListAdapter);
                    ((InventoryActivity) getActivity()).getListAdapter().notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Database error! Unable to update quantity!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener handles when Edit Item button is pressed
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get category position
                int pos;
                switch (category.getText().toString()) {
                    case "Dry Goods": pos = 0;
                        break;
                    case "Freshwater Fish": pos = 1;
                        break;
                    case "Saltwater Fish": pos = 2;
                        break;
                    case "Water": pos = 3;
                        break;
                    default: pos = 0;
                        break;
                }

                // Switch to edit item fragment
                InventoryEditItemFragment fragment = new InventoryEditItemFragment();
                setFragment(fragment);
                fragment.change(description.getText().toString(),
                        sku.getText().toString(),
                        price.getText().toString(),
                        quantity.getText().toString(),
                        category.getText().toString(),
                        pos);
            }
        });

        // Listener handles when Create Item button is pressed
        createItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryCreateItemFragment fragment = new InventoryCreateItemFragment();
                setFragment(fragment);
            }
        });

        // Listener handles when Delete Item button is pressed
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    v.getContext());
            // Set dialog title
            alert.setTitle("Delete Item?");
            // Set dialog message
            alert.setMessage("Item will be deleted!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // Verify that there is always at least one product in the database
                        if(!dbHelper.checkIfOneProductExists()) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Unable to delete! There must be at least one product in database!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Delete item from database
                            dbHelper.deleteProduct(description.getText().toString());
                            Toast.makeText(getActivity().getApplicationContext(), "Item deleted!",
                                    Toast.LENGTH_SHORT).show();
                            change("","","","","");

                            // Update list view adapter to remove deleted item
                            ArrayList<Product> newProducts = dbHelper.getProducts();
                            ListProductAdapter newListAdapter = ((InventoryActivity) getActivity()).getListAdapter();
                            newListAdapter.products = newProducts;

                            // Display updated list
                            ((InventoryActivity) getActivity()).setListAdapter(newListAdapter);
                            ((InventoryActivity) getActivity()).getListAdapter().notifyDataSetChanged();

                            // Update search bar to remove deleted item
                            ArrayAdapter<Product> newSearchAdapter = ((InventoryActivity) getActivity()).getSearchAdapter();
                            newSearchAdapter.clear();
                            newSearchAdapter.addAll(newProducts);
                            ((InventoryActivity) getActivity()).setSearchAdapter(newSearchAdapter);
                            ((InventoryActivity) getActivity()).getSearchAdapter().notifyDataSetChanged();
                        }

                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
            // Create alert dialog
            AlertDialog alertDialog = alert.create();
            // Show alert dialog
            alertDialog.show();
            }
        });

        return view;
    }

    // This method changes what is displayed in detail window when item is chosen from list
    public void change(String txt, String txt1, String txt2, String txt3, String txt4){
        description.setText(txt);
        sku.setText(txt1);
        price.setText(txt2);
        quantity.setText(txt3);
        category.setText(txt4);
    }

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).addToBackStack("").commit();
        fm.executePendingTransactions();
    }
}
