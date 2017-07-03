package com.liquidassets.liquidassetsproject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Byte4: Liquid Assets on 3/19/2016.
 */
public class RestoreDatabaseFragment extends Fragment {
    private ArrayList<File> files;
    private Button restore, cancel, delete;
    private File selectedFile;
    private ListDatabaseAdapter databaseAdapter;
    private ListView databaseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_restore_database, container, false);

        // Create objects
        restore = (Button) view.findViewById(R.id.btnRestoreDatabase);
        cancel = (Button) view.findViewById(R.id.btnCancel);
        delete = (Button) view.findViewById(R.id.btnDeleteDatabase);
        databaseList = (ListView) view.findViewById(R.id.databaseList);

        // Get files from backup database file directory
        files = getFiles();

        // Create user list view adapter
        databaseAdapter = new ListDatabaseAdapter(getActivity().getApplicationContext(), files);
        // Attach adapter to list view
        databaseList.setAdapter(databaseAdapter);
        // Listener to determine what hapens when item is selected from list
        databaseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected file
                selectedFile = (File) databaseList.getItemAtPosition(position);
                databaseList.setSelector(android.R.color.holo_blue_dark);
            }
        });

        // Listener to handle what happens when Restore Database button is clicked
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        v.getContext());
                // Set dialog title
                alert.setTitle("Restore Database?");
                // Set dialog message
                alert.setMessage("Database will be restored!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Restore database
                                try {
                                    // Get directory where files are stored
                                    File data = Environment.getDataDirectory();
                                    // Get path of current database file
                                    String currentDBPath = "//data//com.liquidassets.liquidassetsproject"
                                            + "//databases//liquidAssetsDB.db";
                                    // Get path of backup database file
                                    String backupDBPath = "//data//com.liquidassets.liquidassetsproject"
                                            + "//databases//" + selectedFile.getName();
                                    File backupDB = new File(data, currentDBPath);
                                    File currentDB = new File(data, backupDBPath);
                                    FileChannel src = new FileInputStream(currentDB).getChannel();
                                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                    dst.transferFrom(src, 0, src.size());
                                    src.close();
                                    dst.close();
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Restore Successful!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Restore Failed!",
                                            Toast.LENGTH_SHORT).show();
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

        // Listener to handle what happens when Delete Database button is clicked
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        v.getContext());
                // Set dialog title
                alert.setTitle("Delete Database?");
                // Set dialog message
                alert.setMessage("Database will be deleted!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete file
                                try {
                                    // Get file path
                                    File data = Environment.getDataDirectory();
                                    String dbPath = "//data//com.liquidassets.liquidassetsproject"
                                            + "//databases//" + selectedFile.getName();
                                    File database = new File(data, dbPath);
                                    // Delete file
                                    database.delete();

                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Database Deleted!",
                                            Toast.LENGTH_SHORT).show();

                                    // Update list view to remove deleted item
                                    databaseAdapter.files = getFiles();
                                    databaseAdapter.notifyDataSetChanged();

                                    // Clear highlight
                                    databaseList.setSelector(android.R.color.transparent);
                                }
                                catch(Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Delete Failed!",
                                            Toast.LENGTH_SHORT).show();
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

    private ArrayList<File> getFiles() {
        ArrayList<File> files = new ArrayList<File>();

        // Get path of database files
        File data = Environment.getDataDirectory();
        String dbPath = "//data//com.liquidassets.liquidassetsproject//databases//";
        File f = new File(data, dbPath);

        // Create arrayList of files in directory
        File[] fileList = f.listFiles();
        files = new ArrayList<File>(Arrays.asList(fileList));

        // Remove files that aren't backup db files
        Iterator iterator = files.iterator();
        while(iterator.hasNext()) {
            File file = (File) iterator.next();
            String ext = getFileExtension(file);
            // Remove main db
            if(file.getName().equals("liquidAssetsDB.db")) {
                iterator.remove();
            }
            // Remove non db files
            if(!ext.equals("db")) {
                iterator.remove();
            }
        }
        return files;
    }

    // Method used to determine file extension
    private String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        else {
            return "";
        }
    }

    // Method called to update fragment
    public void setFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, fragment).commit();
        fm.executePendingTransactions();
    }
}
