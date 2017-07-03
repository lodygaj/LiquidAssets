package com.liquidassets.liquidassetsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Byte4: Liquid Assets on 2/1/2016.
 */
public class MainActivity extends AppCompatActivity {
    private boolean isAdmin;
    private Button logout;
    private DBHelper dbHelper;
    private ImageButton inventory, pos, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create objects
        pos = (ImageButton) findViewById(R.id.btnPOS);
        inventory = (ImageButton) findViewById(R.id.btnInventory);
        settings = (ImageButton) findViewById(R.id.btnSettings);
        logout = (Button) findViewById(R.id.btnLogout);

        // Create database helper object
        dbHelper = new DBHelper(this);

        // Get message from log in intent to determine admin status
        Intent intent = getIntent();
        String intentMessage = intent.getStringExtra("USERNAME_DATA");
        isAdmin = dbHelper.isAdmin(intentMessage);

        // Hide settings button if user is not an admin
        if(!isAdmin) {
            settings.setVisibility(View.GONE);
        }

        // Listener to handle what happens when POS button is clicked
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent posStartIntent = new Intent(getApplicationContext(), POSActivity.class);
                startActivity(posStartIntent);
            }
        });

        // Listener to handle what happens when Inventory button is clicked
        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventoryStartIntent = new Intent(getApplicationContext(), InventoryActivity.class);
                startActivity(inventoryStartIntent);
            }
        });

        // Listener to handle what happens when Settings button is clicked
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsStartIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsStartIntent);
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
}
