package com.flexbricks.task.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.flexbricks.task.R;
import com.flexbricks.task.helpers.InputValidation;
import com.flexbricks.task.model.User;
import com.flexbricks.task.sql.UserDAO;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName,textInputLayoutEmail,textInputLayoutPassword,textInputLayoutConfirmPassword;
    private TextInputEditText txtInputEdtName,txtInputEdtEmail,txtInputEdtPassword,txtInputEdtConfirmPassword;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;
    private UserDAO userDAO;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        txtInputEdtName = (TextInputEditText) findViewById(R.id.txtInputEdtName);
        txtInputEdtEmail = (TextInputEditText) findViewById(R.id.txtInputEdtEmail);
        txtInputEdtPassword = (TextInputEditText) findViewById(R.id.txtInputEdtPassword);
        txtInputEdtConfirmPassword = (TextInputEditText) findViewById(R.id.txtInputEdtConfirmPassword);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);


        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);

        inputValidation = new InputValidation(activity);
        userDAO = new UserDAO(activity);
        userDAO.open();

        user = new User();
    }



    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                saveDataToSQLite();
                break;

            case R.id.appCompatTextViewLoginLink:
                finish();
                break;
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private void saveDataToSQLite() {
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
        if (!inputValidation.isInputEditTextMatches(txtInputEdtPassword, txtInputEdtConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }

        if (!userDAO.checkUser(txtInputEdtEmail.getText().toString().trim())) {

            user.setName(txtInputEdtName.getText().toString().trim());
            user.setEmail(txtInputEdtEmail.getText().toString().trim());
            user.setPassword(txtInputEdtPassword.getText().toString().trim());

            userDAO.addUser(user);

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
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
        txtInputEdtConfirmPassword.setText(null);
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
