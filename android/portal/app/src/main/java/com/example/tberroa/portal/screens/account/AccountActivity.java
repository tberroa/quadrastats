package com.example.tberroa.portal.screens.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserData;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqChangeEmail;
import com.example.tberroa.portal.models.requests.ReqChangePassword;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.models.summoner.User;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.authentication.AuthUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.ma_activity_title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_button));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(AccountActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // initialize views
        TextView emailView = (TextView) findViewById(R.id.email_view);
        emailView.setText(new UserData().getEmail(this));

        // initialize buttons
        Button changeEmailButton = (Button) findViewById(R.id.change_email_button);
        Button changePasswordButton = (Button) findViewById(R.id.change_password_button);
        Button signOutButton = (Button) findViewById(R.id.sign_out_button);
        changeEmailButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChangeEmailDialog().show();
            }
        });
        changePasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChangePasswordDialog().show();
            }
        });
        signOutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AuthUtil.signOut(AccountActivity.this);
            }
        });
    }

    private void changeEmailDone(List<EditText> inputFields, ChangeEmailDialog dialog) {
        // extract input fields
        EditText newEmailField = inputFields.get(0);
        EditText passwordField = inputFields.get(1);

        // get the user input
        String newEmail = newEmailField.getText().toString();
        String password = passwordField.getText().toString();

        // create the partially complete request object
        ReqChangeEmail request = new ReqChangeEmail();
        request.new_email = newEmail;
        request.password = password;

        // execute request and close dialog
        new RequestChangeEmail().execute(request);
        dialog.dismiss();
    }

    private void changePasswordDone(List<EditText> inputFields, ChangePasswordDialog dialog) {
        // extract input fields
        EditText currentPasswordField = inputFields.get(0);
        EditText newPasswordField = inputFields.get(1);
        EditText confirmNewPasswordField = inputFields.get(2);

        // get the user input
        String currentPassword = currentPasswordField.getText().toString();
        String newPassword = newPasswordField.getText().toString();
        String confirmNewPassword = confirmNewPasswordField.getText().toString();

        // make sure passwords match
        if (confirmNewPassword.equals(newPassword)) {
            confirmNewPasswordField.setError(null);
        } else { // display error
            confirmNewPasswordField.setError(getResources().getString(R.string.ma_password_mismatch));
            return;
        }

        // create the partially complete request object
        ReqChangePassword request = new ReqChangePassword();
        request.current_password = currentPassword;
        request.new_password = newPassword;

        // execute request and close dialog
        new RequestChangePassword().execute(request);
        dialog.dismiss();
    }

    private class ChangeEmailDialog extends Dialog {

        public ChangeEmailDialog() {
            super(AccountActivity.this, R.style.DialogStyle);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_change_email);
            setCancelable(true);

            // initialize input fields
            EditText newEmailField = (EditText) findViewById(R.id.new_email_field);
            EditText passwordField = (EditText) findViewById(R.id.password_field);
            passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        List<EditText> inputFields = new ArrayList<>();
                        inputFields.add(newEmailField);
                        inputFields.add(passwordField);
                        changeEmailDone(inputFields, ChangeEmailDialog.this);
                    }
                    return false;
                }
            });

            // initialize buttons
            Button doneButton = (Button) findViewById(R.id.done_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<EditText> inputFields = new ArrayList<>();
                    inputFields.add(newEmailField);
                    inputFields.add(passwordField);
                    changeEmailDone(inputFields, ChangeEmailDialog.this);
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    private class ChangePasswordDialog extends Dialog {

        public ChangePasswordDialog() {
            super(AccountActivity.this, R.style.DialogStyle);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_change_password);
            setCancelable(true);

            // initialize input fields
            EditText currentPasswordField = (EditText) findViewById(R.id.current_password_field);
            EditText newPasswordField = (EditText) findViewById(R.id.new_password_field);
            EditText confirmNewPasswordField = (EditText) findViewById(R.id.confirm_new_password_field);
            confirmNewPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        List<EditText> inputFields = new ArrayList<>();
                        inputFields.add(currentPasswordField);
                        inputFields.add(newPasswordField);
                        inputFields.add(confirmNewPasswordField);
                        changePasswordDone(inputFields, ChangePasswordDialog.this);
                    }
                    return false;
                }
            });

            // initialize buttons
            Button doneButton = (Button) findViewById(R.id.done_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<EditText> inputFields = new ArrayList<>();
                    inputFields.add(currentPasswordField);
                    inputFields.add(newPasswordField);
                    inputFields.add(confirmNewPasswordField);
                    changePasswordDone(inputFields, ChangePasswordDialog.this);
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    private class RequestChangeEmail extends AsyncTask<ReqChangeEmail, Void, String> {

        @Override
        protected String doInBackground(ReqChangeEmail... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // complete the request object
            Summoner userSummoner = localDB.summoner(userData.getId(AccountActivity.this));
            ReqChangeEmail request = params[0];
            request.region = userSummoner.region;
            request.key = userSummoner.key;

            // make the request
            String postResponse = "";
            try {
                String url = Constants.URL_CHANGE_EMAIL;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqChangeEmail.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // if request was successful, update user info
            if (postResponse.contains(Constants.VALID_CHANGE_EMAIL)) {
                User user = ModelUtil.fromJson(postResponse, User.class);
                userData.setEmail(AccountActivity.this, user.email);
            }

            return postResponse;
        }

        @Override
        protected void onPostExecute(String postResponse) {
            if (postResponse.contains(Constants.VALID_CHANGE_EMAIL)) {
                User user = ModelUtil.fromJson(postResponse, User.class);
                TextView emailView = (TextView) findViewById(R.id.email_view);
                emailView.setText(user.email);
            } else { // display error
                String message = ScreenUtil.postResponseErrorMessage(postResponse);
                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RequestChangePassword extends AsyncTask<ReqChangePassword, Void, String> {

        @Override
        protected String doInBackground(ReqChangePassword... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // complete the request object
            Summoner user = localDB.summoner(userData.getId(AccountActivity.this));
            ReqChangePassword request = params[0];
            request.region = user.region;
            request.key = user.key;

            // make the request
            String postResponse = "";
            try {
                String url = Constants.URL_CHANGE_PASSWORD;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqChangePassword.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            return postResponse;
        }

        @Override
        protected void onPostExecute(String postResponse) {
            if (postResponse.contains(Constants.VALID_CHANGE_PASSWORD)) {
                String message = getString(R.string.ma_successful_change);
                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
            } else { // display error
                String message = ScreenUtil.postResponseErrorMessage(postResponse);
                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
