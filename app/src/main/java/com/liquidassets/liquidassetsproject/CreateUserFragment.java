package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Byte4: Liquid Assets on 3/19/2016.
 */
public class CreateUserFragment extends Fragment {
    private Boolean admin;
    private Button submit, cancel;
    private DBHelper dbHelper;
    private EditText username, password, passwordConfirm;
    private Matcher matcher;
    private Pattern pattern;
    private RadioGroup adminGroup;
    private User newUser;

    private static final String PASSWORD_PATTERN = "(=.*[a(?=.*\\\\d)(?-z])(?=.*[A-Z]).{8,16})";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_create_user, container, false);

        // Create objects
        submit = (Button) view.findViewById(R.id.btnSubmit);
        cancel = (Button) view.findViewById(R.id.btnCancel);
        username = (EditText) view.findViewById(R.id.edtTxtUsername);
        password = (EditText) view.findViewById(R.id.edtTxtPassword);
        passwordConfirm = (EditText) view.findViewById(R.id.edtTxtConfirmPassword);
        adminGroup = (RadioGroup) view.findViewById(R.id.radioAdmin);
        admin = false;

        // Create database helper object
        dbHelper = new DBHelper(getActivity().getApplicationContext());

        // Listener to handle what happens when Submit button is clicked
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get info entered in fields
                String userName = username.getText().toString();
                String userPassword = password.getText().toString();
                String userPasswordConfirm = passwordConfirm.getText().toString();

                // Check if password matches confirmation password
                if(userPassword.equals(userPasswordConfirm)) {
                    // Check database to see if user already exists
                    if(dbHelper.checkIfUsernameExists(userName)) {
                        // Check if password meets requirements
                        if(validatePassword(userPassword)) {
                            // Submit new user to database
                            newUser = new User(userName,userPassword,admin);

                            try {
                                dbHelper.addUser(newUser);

                                Toast.makeText(getActivity().getApplicationContext(),
                                        "User created successfuly!",
                                        Toast.LENGTH_SHORT).show();

                                // Clear fields
                                username.setText("");
                                password.setText("");
                                passwordConfirm.setText("");
                            }
                            catch (Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Database error! Unable to create user!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Password does not meet criteria!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Username already exists!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Passwords do not match!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener to handle what happens when Cancel button is clicked
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to blank fragment
                BlankFragment fragment = new BlankFragment();
                setFragment(fragment);
            }
        });

        // Listener to handle what happens when Radio button is selected
        adminGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioYes:
                        admin = true;
                        break;
                    case R.id.radioNo:
                        admin = false;
                        break;
                }
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
