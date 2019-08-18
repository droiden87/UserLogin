package com.flexbricks.task.activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.flexbricks.task.R;
import com.flexbricks.task.helpers.InputValidation;
import com.flexbricks.task.model.User;
import com.flexbricks.task.sql.UserDAO;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity  extends AppCompatActivity {


    private static UserDAO userDAO;
    User user;
    private AppCompatActivity activity = EditProfileActivity.this;
    private TextInputLayout textInputLayoutName,textInputLayoutEmail,textInputLayoutPassword;
    private TextInputEditText txtInputEdtName,txtInputEdtEmail,txtInputEdtPassword;
    private NestedScrollView nestedScrollView;
    private List<User> listUsers;
    private InputValidation inputValidation;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_edit_txt));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        userDAO = new UserDAO(activity);
        userDAO.open();

        listUsers = new ArrayList<>();
        inputValidation = new InputValidation(activity);
        user = new User();

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        txtInputEdtName = (TextInputEditText)findViewById(R.id.txtInputEdtName);
        txtInputEdtEmail = (TextInputEditText)findViewById(R.id.txtInputEdtEmail);
        txtInputEdtPassword = (TextInputEditText)findViewById(R.id.txtInputEdtPassword);

         getDataFromSQLite();

    }


    private void getDataFromSQLite() {

        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listUsers.clear();
                listUsers.addAll(userDAO.getAllUser());
                for (int i = 0; i<listUsers.size();i++){
                    user.setName(listUsers.get(i).getName());
                    user.setEmail(listUsers.get(i).getEmail());
                    user.setPassword(listUsers.get(i).getPassword());
                    user.setId(listUsers.get(i).getId());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                txtInputEdtName.setText(user.getName());
                txtInputEdtName.setSelection(txtInputEdtName.getText().length());
                txtInputEdtEmail.setText(user.getEmail());
                txtInputEdtEmail.setSelection(txtInputEdtEmail.getText().length());
            }
        }.execute();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edt_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_btn_save:
               updateUserDataBase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUserDataBase(){
        if (!inputValidation.isInputEditTextFilled(txtInputEdtName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(txtInputEdtEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(txtInputEdtEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(txtInputEdtPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }

        if (!userDAO.checkUser(txtInputEdtEmail.getText().toString().trim())) {
            user.setId(user.getId());
            user.setName(txtInputEdtName.getText().toString().trim());
            user.setEmail(txtInputEdtEmail.getText().toString().trim());
            user.setPassword(txtInputEdtPassword.getText().toString().trim());

            userDAO.updateUser(user);

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(nestedScrollView, getString(R.string.success_update_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();


        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        }


    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        txtInputEdtName.setText(null);
        txtInputEdtEmail.setText(null);
        txtInputEdtPassword.setText(null);

    }

    @Override
    protected void onResume() {
        userDAO.open();
        super.onResume();

    }

    @Override
    protected void onPause() {
        userDAO.close();
        super.onPause();
    }


}
