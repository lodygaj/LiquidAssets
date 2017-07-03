package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Byte4: Liquid Assets on 3/19/2016.
 */
public class ChangePasswordFragment extends Fragment {
    private ArrayList<User> users;
    private Button submit, cancel;
    private DBHelper dbHelper;
    private EditText password, passwordConfirm;
    private ListUserAdapter listAdapter;
    private ListView userList;
    private Matcher matcher;
    private Pattern pattern;
    private TextView username;
    private User selectedUser;

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16})";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_change_password, container, false);

        // Create objects
        submit = (Button) view.findViewById(R.id.btnSubmit);
        cancel = (Button) view.findViewById(R.id.btnCancel);
        password = (EditText) view.findViewById(R.id.edtTxtPassword);
        passwordConfirm = (EditText) view.findViewById(R.id.edtTxtPasswordConfirm);
        username = (TextView) view.findViewById(R.id.txtUsernameDisplay);
        userList = (ListView) view.findViewById(R.id.databaseList);

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());
        // Get list of all users from database
        users = dbHelper.getUsers();

        // Create list view adapter
        listAdapter = new ListUserAdapter(getActivity().getApplicationContext(), users);
        // Attach adapter to list view
        userList.setAdapter(listAdapter);
        // Listener to determine what hapens when item is selected from list view
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected user
                selectedUser = (User) userList.getItemAtPosition(position);
                // Set username in text field
                username.setText(selectedUser.getUsername());
                userList.setSelector(android.R.color.holo_blue_dark);
            }
        });

        // Listener to handle what happens when Submit button is clicked
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get password entered in password field
                String userPassword = password.getText().toString();
                String userPasswordConfirm = passwordConfirm.getText().toString();
                if(userPassword.equals(userPasswordConfirm)) {
                    // Check if password meets requirements
                    if (validatePassword(userPassword)) {
                        // Change password in database
                        try {
                            selectedUser.setPassword(password.getText().toString());
                            dbHelper.editUser(selectedUser);
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Password Changed!",
                                    Toast.LENGTH_SHORT).show();

                            // Clear password fields
                            password.setText("");
                            passwordConfirm.setText("");
                        }
                        catch (Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Database error! Unable to change password!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Password does not meet criteria!",
                                Toast.LENGTH_SHORT).show();

                        // Clear password fields
                        password.setText("");
                        passwordConfirm.setText("");
                    }
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Passwords do not match!",
                            Toast.LENGTH_SHORT).show();

                    // Clear password fields
                    password.setText("");
                    passwordConfirm.setText("");
                }
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

    // Method called to validate whether password meets criteria
    public boolean validatePassword(final String password){
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
