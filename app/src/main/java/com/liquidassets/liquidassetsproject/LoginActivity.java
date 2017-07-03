package com.liquidassets.liquidassetsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Byte4: Liquid Assets on 2/1/2016.
 */
public class LoginActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private EditText editTxtUsername, editTxtPassword;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create objects
        editTxtUsername = (EditText) findViewById(R.id.editTxtUsername);
        editTxtPassword = (EditText) findViewById(R.id.editTxtPassword);

        // Create database helper object
        dbHelper = new DBHelper(this);
    }

    // This method is called when the Login button is clicked
    public void login(View view) {
        username = editTxtUsername.getText().toString();
        password = editTxtPassword.getText().toString();

        // Verify credentials and login to main menu
        if(dbHelper.verifyCredentials(username, password)) {
            Intent mainStartIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainStartIntent.putExtra("USERNAME_DATA", username);
            startActivity(mainStartIntent);
        }
        else {
            Toast.makeText(this, "Invalid username or password!",
                    Toast.LENGTH_SHORT).show();
        }

        // Clear fields
        editTxtUsername.setText("");
        editTxtPassword.setText("");
    }
}
