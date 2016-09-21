package com.quadrastats.screens.account;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.data.LocalDB;
import com.quadrastats.data.UserData;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.requests.ReqChangeEmail;
import com.quadrastats.models.requests.ReqChangePassword;
import com.quadrastats.models.summoner.Summoner;
import com.quadrastats.models.summoner.User;
import com.quadrastats.network.Http;
import com.quadrastats.network.HttpResponse;
import com.quadrastats.screens.BaseActivity;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.authentication.AuthUtil;
import com.quadrastats.screens.home.HomeActivity;

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

        // initialize loading spinner
        int screenWidth = ScreenUtil.screenWidth(this);
        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingSpinner.getLayoutParams().width = (25 * screenWidth) / 100;
        loadingSpinner.getLayoutParams().height = (25 * screenWidth) / 100;
        loadingSpinner.setLayoutParams(loadingSpinner.getLayoutParams());
        loadingSpinner.setVisibility(View.GONE);

        // initialize views
        TextView emailView = (TextView) findViewById(R.id.email_view);
        emailView.setText(new UserData().getEmail(this));

        // initialize buttons
        ImageView changeEmailButton = (ImageView) findViewById(R.id.change_email_button);
        ImageView changePasswordButton = (ImageView) findViewById(R.id.change_password_button);
        ImageView licenseButton = (ImageView) findViewById(R.id.license_button);
        ImageView signOutButton = (ImageView) findViewById(R.id.sign_out_button);
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
        licenseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new LicenseDialog().show();
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
        EditText confirmNewPassField = inputFields.get(2);

        // get the user input
        String currentPassword = currentPasswordField.getText().toString();
        String newPassword = newPasswordField.getText().toString();
        String confirmNewPassword = confirmNewPassField.getText().toString();

        // make sure passwords match
        if (confirmNewPassword.equals(newPassword)) {
            confirmNewPassField.setError(null);
        } else { // display error
            confirmNewPassField.setError(getResources().getString(R.string.ma_password_mismatch));
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

        ChangeEmailDialog() {
            super(AccountActivity.this, R.style.AppTheme_Dialog);
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

        ChangePasswordDialog() {
            super(AccountActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_change_password);
            setCancelable(true);

            // initialize input fields
            EditText currentPasswordField = (EditText) findViewById(R.id.current_password_field);
            EditText newPasswordField = (EditText) findViewById(R.id.new_password_field);
            EditText confirmNewPassField = (EditText) findViewById(R.id.confirm_new_password_field);
            confirmNewPassField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        List<EditText> inputFields = new ArrayList<>();
                        inputFields.add(currentPasswordField);
                        inputFields.add(newPasswordField);
                        inputFields.add(confirmNewPassField);
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
                    inputFields.add(confirmNewPassField);
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

    private class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onBindViewHolder(LicenseViewHolder viewHolder, int i) {
        }

        @Override
        public LicenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.view_licenses, viewGroup, false);
            return new LicenseViewHolder(v);
        }

        class LicenseViewHolder extends RecyclerView.ViewHolder {

            LicenseViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private class LicenseDialog extends Dialog {

        LicenseDialog() {
            super(AccountActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_licenses);
            setCancelable(true);

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setAdapter(new LicenseAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(AccountActivity.this));
        }
    }

    private class RequestChangeEmail extends AsyncTask<ReqChangeEmail, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(ReqChangeEmail... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // complete the request object
            Summoner userSummoner = localDB.summoner(userData.getId(AccountActivity.this));
            ReqChangeEmail request = params[0];
            request.region = userSummoner.region;
            request.key = userSummoner.key;

            // make the request
            HttpResponse postResponse = null;
            try {
                String url = Constants.URL_CHANGE_EMAIL;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqChangeEmail.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // handle the response
            postResponse = ScreenUtil.responseHandler(AccountActivity.this, postResponse);

            if (postResponse.valid) {
                User user = ModelUtil.fromJson(postResponse.body, User.class);
                userData.setEmail(AccountActivity.this, user.email);
            }

            return postResponse;
        }

        @Override
        protected void onPostExecute(HttpResponse postResponse) {
            // turn loading spinner off
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.GONE);

            if (postResponse.valid) {
                User user = ModelUtil.fromJson(postResponse.body, User.class);
                TextView emailView = (TextView) findViewById(R.id.email_view);
                emailView.setText(user.email);
            } else { // display error
                Toast.makeText(AccountActivity.this, postResponse.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // turn loading spinner on
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.VISIBLE);
        }
    }

    private class RequestChangePassword extends AsyncTask<ReqChangePassword, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(ReqChangePassword... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // complete the request object
            Summoner user = localDB.summoner(userData.getId(AccountActivity.this));
            ReqChangePassword request = params[0];
            request.region = user.region;
            request.key = user.key;

            // make the request
            HttpResponse postResponse = null;
            try {
                String url = Constants.URL_CHANGE_PASSWORD;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqChangePassword.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // handle the response
            postResponse = ScreenUtil.responseHandler(AccountActivity.this, postResponse);

            return postResponse;
        }

        @Override
        protected void onPostExecute(HttpResponse postResponse) {
            // turn loading spinner off
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.GONE);

            if (postResponse.valid) {
                String message = getString(R.string.ma_successful_change);
                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
            } else { // display error
                Toast.makeText(AccountActivity.this, postResponse.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // turn loading spinner on
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.VISIBLE);
        }
    }
}
