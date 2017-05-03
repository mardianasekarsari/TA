package com.example.mardiana.alertsystemrumahpompa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Mardiana on 2/19/2017.
 */

public class LoginSQLiteHandler extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "alertsystem";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "nama";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHONE = "telepon_user";
    private static final String KEY_ADDRESS = "alamat_user";
    private static final String KEY_ROLE = "role_user";
    private static final String KEY_RUMAHPOMPA = "rumahpompa";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CREATED_AT = "created_at";

    public LoginSQLiteHandler (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USERNAME + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_ROLE + " TEXT,"
                + KEY_PHONE + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_RUMAHPOMPA + " TEXT," + KEY_PASSWORD + " TEXT" + " )";
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String username, String nama, String role, String alamat, String telepon, String rumahpompa, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, nama); // Name
        values.put(KEY_ROLE, role); // Name
        values.put(KEY_USERNAME, username);
        values.put(KEY_PHONE, telepon);
        values.put(KEY_ADDRESS, alamat);
        values.put(KEY_RUMAHPOMPA, rumahpompa);
        values.put(KEY_PASSWORD, password);

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public void editUser (String username, String nama, String alamat, String telepon){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, nama); // Name
        values.put(KEY_PHONE, telepon);
        values.put(KEY_ADDRESS, alamat);

        // Inserting Row
        db.update(TABLE_USER, values, KEY_USERNAME + "=?", new String[]{username});
        db.close(); // Closing database connection
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put(KEY_USERNAME, cursor.getString(0));
            user.put(KEY_NAME, cursor.getString(1));
            user.put(KEY_ROLE, cursor.getString(2));
            user.put(KEY_PHONE, cursor.getString(3));
            user.put(KEY_ADDRESS, cursor.getString(4));
            user.put(KEY_RUMAHPOMPA, cursor.getString(5));
            user.put(KEY_PASSWORD, cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        //Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        //Log.d(TAG, "Deleted all user info from sqlite");
    }
}
