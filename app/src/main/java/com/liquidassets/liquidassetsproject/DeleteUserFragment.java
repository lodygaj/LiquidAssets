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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Byte4: Liquid Assets on 3/19/2016.
 */
public class DeleteUserFragment extends Fragment {
    private ArrayList<User> users;
    private Button delete, cancel;
    private DBHelper dbHelper;
    private ListUserAdapter listAdapter;
    private ListView userList;
    private User selectedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_delete_user, container, false);

        // Create objects
        delete = (Button) view.findViewById(R.id.btnRestoreDatabase);
        cancel = (Button) view.findViewById(R.id.btnCancel);
        userList = (ListView) view.findViewById(R.id.databaseList);

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // Get list of all users in database
        try {
            users = dbHelper.getUsers();
        }
        catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Database error! Unable to get list of users!",
                    Toast.LENGTH_SHORT).show();
        }

        // Create user list view adapter
        listAdapter = new ListUserAdapter(getActivity().getApplicationContext(), users);
        // Attach adapter to list view
        userList.setAdapter(listAdapter);
        // Listener to determine what hapens when item is selected from list
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected user
                selectedUser = users.get(position);
                userList.setSelector(android.R.color.holo_blue_dark);
            }
        });

        // Listener to handle what happens when Cancel button is clicked
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        v.getContext());
                // Set dialog title
                alert.setTitle("Delete User?");
                // Set dialog message
                alert.setMessage("User will be deleted!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Verify that there is always at least one admin user in the database
                                if(selectedUser.getAdmin() && !dbHelper.checkIfAdminExists() ) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "There must be at least one admin user!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    // Delete item from database
                                    try {
                                        dbHelper.deleteUser(selectedUser.getUsername());
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "User deleted!",
                                                Toast.LENGTH_SHORT).show();

                                        // Refresh list view
                                        users = dbHelper.getUsers();
                                        listAdapter.users = users;
                                        listAdapter.notifyDataSetChanged();

                                        // Clear highlight
                                        userList.setSelector(android.R.color.transparent);
                                    }
                                    catch (Exception e) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Database error! Unable to delete user!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
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

        // Listener to handle what happens when Cancel button is clicked
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
