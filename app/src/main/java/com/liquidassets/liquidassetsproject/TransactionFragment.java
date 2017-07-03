package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Byte4: LiquidAssets on 4/24/2016.
 */
public class TransactionFragment extends Fragment {
    private CalendarView calendar;
    private DBHelper dbHelper;
    private String currentDate;
    private TextView todaySubtotal, todayTax, todayTotal, selectedSubtotal, selectedTax, selectedTotal,
        selectedSubtotalText, selectedTaxText, selectedTotalText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_transactions, container, false);

        // Create objects
        todaySubtotal = (TextView) view.findViewById(R.id.txtSubtotalDisplay);
        todayTax = (TextView) view.findViewById(R.id.txtTaxDisplay);
        todayTotal = (TextView) view.findViewById(R.id.txtTotalDisplay);
        selectedSubtotal = (TextView) view.findViewById(R.id.txtSelectedSubtotalDisplay);
        selectedTax = (TextView) view.findViewById(R.id.txtSelectedTaxDisplay);
        selectedTotal = (TextView) view.findViewById(R.id.txtSelectedTotalDisplay);
        selectedSubtotalText = (TextView) view.findViewById(R.id.txtSelectedSubtotal);
        selectedTaxText = (TextView) view.findViewById(R.id.txtSelectedTax);
        selectedTotalText = (TextView) view.findViewById(R.id.txtSelectedTotal);
        calendar = (CalendarView) view.findViewById(R.id.calendar);

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // Hide selected day text
        selectedSubtotalText.setVisibility(view.GONE);
        selectedTaxText.setVisibility(view.GONE);
        selectedTotalText.setVisibility(view.GONE);

        // Get current date in string format
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date today = Calendar.getInstance().getTime();
        currentDate = df.format(today);

        // Get array list of all transactions for current date from database
        ArrayList<Transaction> todaysTransactions = dbHelper.getTransactionsByDate(currentDate);

        // Calculate totals
        double todaysSubtotal = 0;
        double todaysTax = 0;
        double todaysTotal = 0;
        for(Transaction trans: todaysTransactions) {
            todaysSubtotal += trans.getSubtotal();
            todaysTax += trans.getTax();
            todaysTotal += trans.getTotal();
        }

        // Format numbers into currency strings to display on screen
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String subtotal = formatter.format(todaysSubtotal);
        String tax = formatter.format(todaysTax);
        String total = formatter.format(todaysTotal);

        // Set totals in fragment
        if(todaysSubtotal != 0) {
            todaySubtotal.setText(subtotal);
            todayTax.setText(tax);
            todayTotal.setText(total);
        }
        else {
            todaySubtotal.setText("$0.00");
            todayTax.setText("$0.00");
            todayTotal.setText("$0.00");
        }

        // Listener to handle when a date is selected
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Get selected date in MM-dd-yyyy string format
                DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                Date day = new Date(year - 1900, month, dayOfMonth);
                String selectedDate = df.format(day);

                // Get array list of all transactions for current date from database
                ArrayList<Transaction> daysTransactions = dbHelper.getTransactionsByDate(selectedDate);

                // Calculate totals
                double daysSubtotal = 0;
                double daysTax = 0;
                double daysTotal = 0;
                for(Transaction trans: daysTransactions) {
                    daysSubtotal += trans.getSubtotal();
                    daysTax += trans.getTax();
                    daysTotal += trans.getTotal();
                }

                // Format numbers into currency strings to display on screen
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String subtotal = formatter.format(daysSubtotal);
                String tax = formatter.format(daysTax);
                String total = formatter.format(daysTotal);

                // Set totals in fragment
                if(daysSubtotal != 0) {
                    selectedSubtotal.setText(subtotal);
                    selectedTax.setText(tax);
                    selectedTotal.setText(total);
                }
                else {
                    selectedSubtotal.setText("$0.00");
                    selectedTax.setText("$0.00");
                    selectedTotal.setText("$0.00");
                }

                // Show selected day text
                selectedSubtotalText.setVisibility(view.VISIBLE);
                selectedTaxText.setVisibility(view.VISIBLE);
                selectedTotalText.setVisibility(view.VISIBLE);
            }
        });

        return view;
    }
}
