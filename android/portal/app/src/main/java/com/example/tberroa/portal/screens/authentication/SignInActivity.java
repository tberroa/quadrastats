package com.example.tberroa.portal.screens.authentication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.UserData;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqResetPassword;
import com.example.tberroa.portal.models.requests.ReqSignIn;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.models.summoner.User;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;

import java.io.IOException;

public class SignInActivity extends AppCompatActivity {

    private boolean inView;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // no animation if starting activity as a reload
        if ((getIntent().getAction() != null) && getIntent().getAction().equals(Constants.UI_RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // check if user is already signed in
        if (new UserData().isSignedIn(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        // resize layout according to screen
        int layoutWidth = (80 * ScreenUtil.screenWidth(this)) / 100;
        LinearLayout signInLayout = (LinearLayout) findViewById(R.id.sign_in_layout);
        signInLayout.getLayoutParams().width = layoutWidth;
        signInLayout.setLayoutParams(signInLayout.getLayoutParams());

        // initialize input fields
        EditText keyField = (EditText) findViewById(R.id.summoner_name_field);
        EditText passwordField = (EditText) findViewById(R.id.password_field);
        Spinner regionSelect = (Spinner) findViewById(R.id.region_select_spinner);
        int color = ContextCompat.getColor(this, R.color.accent);
        regionSelect.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        // initialize buttons
        signInButton = (Button) findViewById(R.id.sign_in_button);
        TextView resetPasswordButton = (TextView) findViewById(R.id.reset_password_view);
        TextView goToRegisterButton = (TextView) findViewById(R.id.go_to_register_view);
        signInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                signInButton.setEnabled(false);

                // get user inputs
                String key = keyField.getText().toString();
                String password = passwordField.getText().toString();

                // make sure a region is selected
                String region;
                int regionSelection = regionSelect.getSelectedItemPosition();
                if (regionSelection > 0) {
                    region = AuthUtil.decodeRegion(regionSelection);
                } else { // display error
                    String message = getString(R.string.err_select_region);
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                    signInButton.setEnabled(true);
                    return;
                }

                // create the request object
                ReqSignIn request = new ReqSignIn();
                request.region = region;
                request.key = key;
                request.password = password;

                // execute the request
                new RequestSignIn().execute(request);
            }
        });
        resetPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new ResetPasswordDialog().show();
            }
        });
        goToRegisterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Constants.UI_RELOAD);
                startActivity(intent);
                finish();
            }
        });

        // populate region select spinner
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.auth_select_region_array, R.layout.spinner_textview);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_textview);
        regionSelect.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inView = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        inView = false;
    }

    private class RequestResetPassword extends AsyncTask<ReqResetPassword, Void, String> {

        @Override
        protected String doInBackground(ReqResetPassword... params) {
            // extract the request object
            ReqResetPassword request = params[0];

            // make the request
            String postResponse = "";
            try {
                String url = Constants.URL_RESET_PASSWORD;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqResetPassword.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            return postResponse;
        }

        @Override
        protected void onPostExecute(String postResponse) {
            if (postResponse.contains(Constants.VALID_RESET_PASSWORD)) {
                String message = getString(R.string.auth_successful_reset);
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
            } else { // display error
                String message = ScreenUtil.postResponseErrorMessage(SignInActivity.this, postResponse);
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RequestSignIn extends AsyncTask<ReqSignIn, Void, String> {

        @Override
        protected String doInBackground(ReqSignIn... params) {
            // extract the request object
            ReqSignIn request = params[0];

            // make the request
            String postResponse = "";
            try {
                String url = Constants.URL_SIGN_IN;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqSignIn.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            return postResponse;
        }

        @Override
        protected void onPostExecute(String postResponse) {
            if (postResponse.contains(Constants.VALID_SIGN_IN)) {
                // get the summoner object
                Summoner summoner = ModelUtil.fromJson(postResponse, Summoner.class);

                // get the user object
                User user = ModelUtil.fromJson(postResponse, User.class);

                // sign in
                AuthUtil.signIn(SignInActivity.this, summoner, user, inView);
            } else { // display error
                String message = ScreenUtil.postResponseErrorMessage(SignInActivity.this, postResponse);
                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                signInButton.setEnabled(true);
            }
        }
    }

    private class ResetPasswordDialog extends Dialog {

        public ResetPasswordDialog() {
            super(SignInActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_reset_password);
            setCancelable(true);

            // initialize input fields
            EditText emailField = (EditText) findViewById(R.id.email_field);
            EditText summonerNameField = (EditText) findViewById(R.id.summoner_name_field);
            Spinner regionSelect = (Spinner) findViewById(R.id.region_select_spinner);

            // populate region select spinner
            ArrayAdapter<CharSequence> adapter;
            int array = R.array.auth_select_region_array;
            adapter = ArrayAdapter.createFromResource(SignInActivity.this, array, R.layout.spinner_textview);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_textview);
            regionSelect.setAdapter(adapter);

            // initialize buttons
            Button doneButton = (Button) findViewById(R.id.done_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // make sure a region is selected
                    String region;
                    int regionSelection = regionSelect.getSelectedItemPosition();
                    if (regionSelection > 0) {
                        region = AuthUtil.decodeRegion(regionSelection);
                    } else { // display error
                        String message = getString(R.string.err_select_region);
                        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // create the request object
                    ReqResetPassword request = new ReqResetPassword();
                    request.region = region;
                    request.key = summonerNameField.getText().toString();
                    request.email = emailField.getText().toString();

                    // execute the request
                    new RequestResetPassword().execute(request);

                    dismiss();
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
}