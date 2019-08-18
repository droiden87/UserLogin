package com.flexbricks.task.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.flexbricks.task.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO{

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;
    private Context context;

    public UserDAO(Context context) {
        this.context = context;
        databaseHelper = DatabaseHelper.getHelper(context);
        open();

    }

    public void open() throws SQLException {
        if (databaseHelper == null)
            databaseHelper = DatabaseHelper.getHelper(context);
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        //sqLiteDatabase.setForeignKeyConstraintsEnabled(true);
    }

    public void close() {
        databaseHelper.close();
    }


    /**
     * This method is to create user record
     *
     * @param user
     */
    public void addUser(User user) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());

        // Inserting Row
        sqLiteDatabase.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public List<User> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_USER_EMAIL,
                DatabaseHelper.COLUMN_USER_NAME,
                DatabaseHelper.COLUMN_USER_PASSWORD
        };
        // sorting orders
        String sortOrder =
                DatabaseHelper.COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = sqLiteDatabase.query( DatabaseHelper.TABLE_USERS, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex( DatabaseHelper.COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex( DatabaseHelper.COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex( DatabaseHelper.COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex( DatabaseHelper.COLUMN_USER_PASSWORD)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();


        // return user list
        return userList;
    }

    /**
     * This method to update user record
     *
     * @param user
     */
    public void updateUser(User user) {

        ContentValues values = new ContentValues();
        values.put( DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put( DatabaseHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put( DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());

        // updating row
        sqLiteDatabase.update( DatabaseHelper.TABLE_USERS, values,  DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    /**
     * This method is to delete user record
     *
     * @param user
     */
    public void deleteUser(User user) {
        // delete user record by id
        sqLiteDatabase.delete( DatabaseHelper.TABLE_USERS,  DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID
        };

        // selection criteria
        String selection =  DatabaseHelper.COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'johnDoe@gmail.com';
         */
        Cursor cursor = sqLiteDatabase.query( DatabaseHelper.TABLE_USERS, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID
        };
        // selection criteria
        String selection =  DatabaseHelper.COLUMN_USER_EMAIL + " = ?" + " AND " +  DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'johnDoe@gmail.com' AND user_password = 'qwerty';
         */
        Cursor cursor = sqLiteDatabase.query( DatabaseHelper.TABLE_USERS, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }





    public long getUserID(String userName) {
        long userId = 0;

        String rawQuery = "SELECT users.user_id FROM users WHERE users.user_name = " + "'"+ userName +"'";;

        Cursor cursor = sqLiteDatabase.rawQuery(rawQuery, null);


        if (cursor != null && cursor.moveToNext()) {
            while (!cursor.isAfterLast()) {
                userId = cursor.getLong(0);
                cursor.moveToNext();
            }

        }


        cursor.moveToNext();
        // make sure to close the cursor
        cursor.close();
        return userId;
    }
}
