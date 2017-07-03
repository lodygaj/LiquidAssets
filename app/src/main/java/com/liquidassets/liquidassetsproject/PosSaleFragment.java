package com.liquidassets.liquidassetsproject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Byte4: Liquid Assets on 2/21/2016.
 */
public class PosSaleFragment extends Fragment {
    private ArrayList<SaleItem> items;
    private Button add, finalize, remove;
    private DBHelper dbHelper;
    private EditText percentOff;
    private ListView saleList;
    private TextView subtotal, tax, total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pos_sale, container, false);

        // Create objects
        saleList = (ListView) view.findViewById(R.id.saleList);
        subtotal = (TextView) view.findViewById(R.id.txtSubtotalDisplay);
        tax = (TextView) view.findViewById(R.id.txtTaxDisplay);
        total = (TextView) view.findViewById(R.id.txtTotalDisplay);
        percentOff = (EditText) view.findViewById(R.id.edtTxtPercentOffTotal);
        add = (Button) view.findViewById(R.id.btnAdd);
        finalize = (Button) view.findViewById(R.id.btnFinalize);
        remove = (Button) view.findViewById(R.id.btnRemove);

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // Get array of products for sale window
        items = getItems();

        // Create product list view adapter
        SaleProductAdapter adapter = new SaleProductAdapter(getActivity(), items);
        // Attach adapter to list view
        saleList.setAdapter(adapter);
        // Listener to handle when list item is clicked
        saleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show product detail in fragment
                PosEditItemFragment fragment = new PosEditItemFragment();
                setFragment(fragment);
                fragment.setFields(items.get(position).getDescription(),
                        Double.toString(items.get(position).getPrice()),
                        Integer.toString(items.get(position).getQuantity()),
                        Integer.toString(items.get(position).getPercentOff()));
                fragment.setIsDiscounted(items.get(position).isDiscount());
            }
        });

        // Listener to handle what happens when Add button is clicked
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get array list of sale items
                ArrayList<SaleItem> temp = ((POSActivity) getActivity()).getSaleList();

                // If percent off is entered
                if(!percentOff.getText().toString().equals("")) {
                    // Get percent to take off sale
                    int discount = Integer.parseInt(percentOff.getText().toString());

                    // Calculate new totals
                    double tempTotal = 0;
                    for(SaleItem item: temp) {
                        tempTotal += item.getExtPrice();
                    }
                    tempTotal = tempTotal * (1 - ((double) discount / 100));
                    // Format numbers into currency strings to display on screen
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    String subtotal = formatter.format(tempTotal);
                    String tax = formatter.format(tempTotal * .06);
                    String total = formatter.format(tempTotal * 1.06);

                    // Switch fragment to sale window
                    PosSaleFragment saleFragment = new PosSaleFragment();
                    setFragment(saleFragment);

                    // Update totals in sale fragment
                    saleFragment.updateTotals(subtotal, tax, total);
                }

            }
        });

        // Listener to handle what happens when Remove button is clicked
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get array list of sale items
                ArrayList<SaleItem> temp = ((POSActivity) getActivity()).getSaleList();

                // Calculate new totals
                double tempTotal = 0;
                for(SaleItem item: temp) {
                    tempTotal += item.getExtPrice();
                }

                // Format numbers into currency strings to display on screen
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String subtotal = formatter.format(tempTotal);
                String tax = formatter.format(tempTotal * .06);
                String total = formatter.format(tempTotal * 1.06);

                // Switch fragment to sale window
                PosSaleFragment saleFragment = new PosSaleFragment();
                setFragment(saleFragment);

                // Update totals
                saleFragment.updateTotals(subtotal, tax, total);
            }
        });

        // Listener to handle what happens when Finalize button is clicked
        finalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        v.getContext());
                // Set dialog title
                alert.setTitle("Finalize Sale?");
                // Set dialog message
                alert.setMessage("Sale will be finalized!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // Get array list of sale items
                                ArrayList<SaleItem> temp = ((POSActivity) getActivity()).getSaleList();

                                // Remove items from database
                                for(SaleItem item: temp) {
                                    Product tempProduct = dbHelper.getProduct(item.getDescription());
                                    tempProduct.setQuantity(tempProduct.getQuantity() - item.getQuantity());
                                    dbHelper.editProduct(tempProduct, item.getDescription());
                                }

                                // Submit transaction to database
                                addTransactionData(temp);

                                // Clear sale list
                                ((POSActivity) getActivity()).setSaleList(new ArrayList<SaleItem>());

                                // Update list view to show updated items
                                ArrayList<Product> newProducts = dbHelper.getProducts();
                                ListProductAdapter newListAdapter = ((POSActivity) getActivity()).getListAdapter();
                                newListAdapter.products = newProducts;

                                // Display updated list
                                ((POSActivity) getActivity()).setListAdapter(newListAdapter);
                                ((POSActivity) getActivity()).getListAdapter().notifyDataSetChanged();

                                // Switch fragment to blank window
                                BlankFragment blankFragment = new BlankFragment();
                                setFragment(blankFragment);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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

    private void addTransactionData(ArrayList<SaleItem> sale) {
        // Get date
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date today = Calendar.getInstance().getTime();
        String date = df.format(today);

        // Get time
        df = new SimpleDateFormat("HH:mm");
        today = Calendar.getInstance().getTime();
        String time = df.format(today);

        // Get subtotal
        double transSubtotal = 0;
        for(SaleItem item: sale) {
            transSubtotal += item.getExtPrice();
        }

        // Get tax
        double transTax = transSubtotal * .06;
        // Round to two decimals
        transTax = Math.round(transTax * 100.0) / 100.0;

        // Get total
        double transTotal = transSubtotal * 1.06;
        // Round to two decimals
        transTotal =  Math.round(transTotal * 100.0) / 100.0;

        // Submit transaction to database
        dbHelper.addTransaction(date, time, transSubtotal, transTax, transTotal);
    }

    // Create array of products to display in sale window list view
    private ArrayList<SaleItem> getItems() {
        ArrayList<SaleItem> products = new ArrayList<>();
        products = ((POSActivity)getActivity()).getSaleList();
        return products;
    }

    // Set totals in sale window
    public void updateTotals(String txt, String txt1, String txt2) {
        subtotal.setText(txt);
        tax.setText(txt1);
        total.setText(txt2);
    }

    // Method called to upgrade fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}