package com.liquidassets.liquidassetsproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Byte4: Liquid Assets on 3/1/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "liquidAssetsDB.db";

    //table USERS with columns
    private static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ADMIN = "admin";

    //table PRODUCTS with columns
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PROD_ID = "_id";
    private static final String COLUMN_SKU = "sku";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_CATEGORY = "category";

    //table TRANSACTIONS with columns
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_TRANSACTION_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_SUBTOTAL = "subtotal";
    private static final String COLUMN_TAX = "tax";
    private static final String COLUMN_TOTAL = "total";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " +
                TABLE_USERS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " STRING NOT NULL, " +
                COLUMN_PASSWORD + " STRING NOT NULL, " +
                COLUMN_ADMIN + " BOOLEAN NOT NULL )";
        db.execSQL(CREATE_USERS_TABLE);

        //create Products table
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_PRODUCTS + "(" +
                COLUMN_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SKU + " INTEGER, " +
                COLUMN_DESCRIPTION + " STRING NOT NULL, " +
                COLUMN_PRICE + " DOUBLE NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_CATEGORY + " STRING )";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        //create Transactions table
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " +
                TABLE_TRANSACTIONS + "(" +
                COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " STRING NOT NULL, " +
                COLUMN_TIME + " STRING NOT NULL, " +
                COLUMN_SUBTOTAL + " DOUBLE NOT NULL, " +
                COLUMN_TAX + " DOUBLE NOT NULL, " +
                COLUMN_TOTAL + " DOUBLE NOT NULL )";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);

        //insert initial admin user
        db.execSQL("INSERT INTO " + TABLE_USERS + " VALUES ('1','admin','Admin123','1');");

        //insert initial sample product
        db.execSQL("INSERT INTO " + TABLE_PRODUCTS + " VALUES ('1','111111','Sample','1.99','1','Freshwater Fish');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    //inserts a new user into database
    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ADMIN, user.getAdmin());
        db.insert(TABLE_USERS, null, values);
    }

    //inserts a new product into database
    public void addProduct(Product product){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_SKU, product.getSku());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_CATEGORY, product.getCategory());
        db.insert(TABLE_PRODUCTS, null, values);
    }

    public void addTransaction(String date, String time, double subtotal, double tax, double total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_SUBTOTAL, subtotal);
        values.put(COLUMN_TAX, tax);
        values.put(COLUMN_TOTAL, total);
        db.insert(TABLE_TRANSACTIONS, null, values);
    }

    //edits a users details in database
    public void editUser(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ADMIN, user.getAdmin());
        db.update(TABLE_USERS, values, "username = ? ", new String[]{user.getUsername()});
    }

    //edits a products details in database
    public void editProduct(Product product, String oldDescription) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_SKU, product.getSku());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_CATEGORY, product.getCategory());
        db.update(TABLE_PRODUCTS, values, "description = ? ", new String[] {oldDescription} );
    }

    //deletes a user from database
    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USERNAME + " = '" + username + "'", null);
    }

    //deletes a product from database
    public void deleteProduct(String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_DESCRIPTION + " = '" + description + "'", null);
    }

    //retrieves a product from database
    public Product getProduct(String desc) {
        Product product;
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_DESCRIPTION + " = '" + desc + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
        String description = cursor.getString(descriptionIndex);

        int skuIndex = cursor.getColumnIndexOrThrow(COLUMN_SKU);
        String sku = cursor.getString(skuIndex);

        int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
        String price = cursor.getString(priceIndex);

        int quantityIndex = cursor.getColumnIndexOrThrow(COLUMN_QUANTITY);
        String quantity = cursor.getString(quantityIndex);

        int categoryIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY);
        String category = cursor.getString(categoryIndex);

        product = new Product(description, Integer.parseInt(sku), Double.parseDouble(price), Integer.parseInt(quantity), category);

        return product;
    }

    //checks if the passed user name exists in the database
    public boolean checkIfUsernameExists(String name){
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = '" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() != 0){
            db.close();
            return false;
        }else
            db.close();
            return true;
    }

    //checks if the product already exists in the database
    public boolean checkIfProductExists(Product product){
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_DESCRIPTION + " = '" + product.getDescription() + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() != 0){
            db.close();
            return true;
        }else
            db.close();
        return false;
    }

    //checks to make sure there is at least one admin user in the database
    public boolean checkIfAdminExists(){
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ADMIN + " = '1'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() > 1){
            db.close();
            return true;
        }else
            db.close();
        return false;
    }

    //checks if the passed user name exists in the database
    public boolean isAdmin(String username){
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = '" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        int adminIndex = cursor.getColumnIndexOrThrow(COLUMN_ADMIN);
        String admin = cursor.getString(adminIndex);

        if(admin.equals("1")) {
            return true;
        }
        else {
            return false;
        }
    }

    //checks to make sure there is at least one product in the database
    public boolean checkIfOneProductExists() {
        String query = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() > 1) {
            db.close();
            return true;
        }else
            db.close();
        return false;
    }

    //checks if username and password are valid credentials for login
    public boolean verifyCredentials(String username, String password) {
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = '"
                + username + "' AND " + COLUMN_PASSWORD + " = '" + password + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() != 0){
            db.close();
            return true;
        }else
            db.close();
        return false;
    }

    //returns an arraylist of products from database with given category
    public ArrayList<Product> getProductsByCategory(String category) {
        ArrayList<Product> products = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_CATEGORY + " = '" + category + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {

            int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
            String description = cursor.getString(descriptionIndex);

            int skuIndex = cursor.getColumnIndexOrThrow(COLUMN_SKU);
            String sku = cursor.getString(skuIndex);

            int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
            String price = cursor.getString(priceIndex);

            int quantityIndex = cursor.getColumnIndexOrThrow(COLUMN_QUANTITY);
            String quantity = cursor.getString(quantityIndex);

            Product product = new Product(description, Integer.parseInt(sku), Double.parseDouble(price), Integer.parseInt(quantity), category);
            products.add(product);

            cursor.moveToNext();
        }

        // Sort arraylist alphabetically
        Collections.sort(products, new Comparator<Product>() {
            public int compare(Product p1, Product p2) {
                return p1.getDescription().compareTo(p2.getDescription());
            }
        });

        return products;
    }

    //returns an arraylist of products from database
    public ArrayList<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {

            int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
            String description = cursor.getString(descriptionIndex);

            int skuIndex = cursor.getColumnIndexOrThrow(COLUMN_SKU);
            String sku = cursor.getString(skuIndex);

            int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
            String price = cursor.getString(priceIndex);

            int quantityIndex = cursor.getColumnIndexOrThrow(COLUMN_QUANTITY);
            String quantity = cursor.getString(quantityIndex);

            int categoryIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY);
            String category = cursor.getString(categoryIndex);

            Product product = new Product(description, Integer.parseInt(sku), Double.parseDouble(price), Integer.parseInt(quantity), category);
            products.add(product);

            cursor.moveToNext();
        }

        // Sort arraylist alphabetically
        Collections.sort(products, new Comparator<Product>() {
            public int compare(Product p1, Product p2) {
                return p1.getDescription().compareTo(p2.getDescription());
            }
        });

        return products;
    }

    //returns an arraylist of users from database
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {

            int usernameIndex = cursor.getColumnIndexOrThrow(COLUMN_USERNAME);
            String username = cursor.getString(usernameIndex);

            int passwordIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD);
            String password = cursor.getString(passwordIndex);

            int adminIndex = cursor.getColumnIndexOrThrow(COLUMN_ADMIN);
            String admin = cursor.getString(adminIndex);

            if(admin.equals("1")) {
                admin = "true";
            }
            else {
                admin = "false";
            }

            User user = new User(username, password, Boolean.parseBoolean(admin));
            users.add(user);

            cursor.moveToNext();
        }

        // Sort arraylist alphabetically
        Collections.sort(users, new Comparator<User>() {
            public int compare(User p1, User p2) {
                return p1.getUsername().compareTo(p2.getUsername());
            }
        });

        return users;
    }

    //returns an arraylist of transactions from database with given date
    public ArrayList<Transaction> getTransactionsByDate(String givenDate) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_DATE + " = '" + givenDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {

            int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE);
            String date = cursor.getString(dateIndex);

            int timeIndex = cursor.getColumnIndexOrThrow(COLUMN_TIME);
            String time = cursor.getString(timeIndex);

            int subtotalIndex = cursor.getColumnIndexOrThrow(COLUMN_SUBTOTAL);
            String subtotal = cursor.getString(subtotalIndex);

            int taxIndex = cursor.getColumnIndexOrThrow(COLUMN_TAX);
            String tax = cursor.getString(taxIndex);

            int totalIndex = cursor.getColumnIndexOrThrow(COLUMN_TOTAL);
            String total = cursor.getString(totalIndex);

            Transaction trans = new Transaction(date, time, Double.parseDouble(subtotal),
                    Double.parseDouble(tax), Double.parseDouble(total));
            transactions.add(trans);

            cursor.moveToNext();
        }

        return transactions;
    }
}
