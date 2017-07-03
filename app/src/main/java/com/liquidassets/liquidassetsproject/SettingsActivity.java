package com.liquidassets.liquidassetsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Byte4: Liquid Assets on 2/1/2016.
 */
public class SettingsActivity extends AppCompatActivity {
    private Button createUser, deleteUser, changePassword, backupDatabase, restoreDatabase, viewSales, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Create objects
        createUser = (Button) findViewById(R.id.btnCreateUser);
        deleteUser = (Button) findViewById(R.id.btnDeleteUser);
        changePassword = (Button) findViewById(R.id.btnChangePassword);
        backupDatabase = (Button) findViewById(R.id.btnBackupDatabase);
        restoreDatabase = (Button) findViewById(R.id.btnRestoreDatabase);
        viewSales = (Button) findViewById(R.id.btnViewSales);
        logout = (Button) findViewById(R.id.btnLogout);

        // Listener to handle what happens when Create New User button is clicked
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUserFragment fragment = new CreateUserFragment();
                setFragment(fragment);
            }
        });

        // Listener to handle what happens when Delete User button is clicked
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteUserFragment fragment = new DeleteUserFragment();
                setFragment(fragment);
            }
        });

        // Listener to handle what happens when Change Password button is clicked
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment fragment = new ChangePasswordFragment();
                setFragment(fragment);
            }
        });

        // Listener to handle what happens when Backup Database button is clicked
        backupDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create backup filename by using current date and time
                DateFormat df = new SimpleDateFormat("MM-dd-yyyy--HH:mm");
                Date today = Calendar.getInstance().getTime();
                String newFilename = df.format(today) + ".db";

                try {
                    // Get directory where files are stored
                    File data = Environment.getDataDirectory();
                    // Get path of current database file
                    String currentDBPath = "//data//com.liquidassets.liquidassetsproject"
                            + "//databases//liquidAssetsDB.db";
                    // Get path where backup database file will be stored
                    String backupDBPath = "//data//com.liquidassets.liquidassetsproject"
                            + "//databases//" + newFilename;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(data, backupDBPath);
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(),
                            "Backup Successful!",
                            Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Backup Failed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener to handle what happens when Create New User button is clicked
        restoreDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreDatabaseFragment fragment = new RestoreDatabaseFragment();
                setFragment(fragment);
            }
        });

        // Listener to handle what happens when transactions button is clicked
        viewSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionFragment fragment = new TransactionFragment();
                setFragment(fragment);
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

    // Method called to upgrade fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
