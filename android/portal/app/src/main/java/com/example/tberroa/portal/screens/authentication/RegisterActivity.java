package com.example.tberroa.portal.screens.authentication;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.example.tberroa.portal.models.requests.ReqRegister;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.models.summoner.User;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;

import java.io.IOException;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private boolean inView;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        LinearLayout registerLayout = (LinearLayout) findViewById(R.id.register_layout);
        registerLayout.getLayoutParams().width = layoutWidth;
        registerLayout.setLayoutParams(registerLayout.getLayoutParams());


        // initialize input fields
        EditText keyField = (EditText) findViewById(R.id.summoner_name_field);
        EditText emailField = (EditText) findViewById(R.id.email_field);
        EditText passwordField = (EditText) findViewById(R.id.password_field);
        EditText confirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);
        Spinner regionSelect = (Spinner) findViewById(R.id.region_select_spinner);

        // initialize buttons
        registerButton = (Button) findViewById(R.id.register_button);
        TextView goToSignInButton = (TextView) findViewById(R.id.go_to_sign_in_view);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerButton.setEnabled(false);

                // extract user inputs
                String key = keyField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();

                // make sure passwords match
                if (confirmPassword.equals(password)) {
                    confirmPasswordField.setError(null);
                } else { // display error
                    confirmPasswordField.setError(getResources().getString(R.string.auth_password_mismatch));
                    registerButton.setEnabled(true);
                    return;
                }

                // make sure a region is selected
                String region;
                int regionSelection = regionSelect.getSelectedItemPosition();
                if (regionSelection > 0) {
                    region = AuthUtil.decodeRegion(regionSelection);
                } else { // display error
                    String message = getString(R.string.err_select_region);
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    registerButton.setEnabled(true);
                    return;
                }

                // initialize validation code
                int code = new Random().nextInt(80000 - 65000) + 15000;
                String codeString = Integer.toString(code);

                // create request object
                ReqRegister request = new ReqRegister();
                request.region = region;
                request.key = key;
                request.email = email;
                request.password = password;
                request.code = codeString;

                // display dialog
                new ValidateDialog(request).show();
            }
        });
        goToSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Constants.UI_RELOAD);
                startActivity(intent);
                finish();
            }
        });

        // initialize region select spinner
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

    private class RequestRegister extends AsyncTask<ReqRegister, Void, String> {

        @Override
        protected String doInBackground(ReqRegister... params) {
            // extract the request object
            ReqRegister request = params[0];

            // make the request
            String postResponse = "";
            try {
                String url = Constants.URL_REGISTER;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqRegister.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            return postResponse;
        }

        @Override
        protected void onPostExecute(String postResponse) {
            if (postResponse.contains(Constants.VALID_REGISTER)) {
                // get the summoner object
                Summoner summoner = ModelUtil.fromJson(postResponse, Summoner.class);

                // get the user object
                User user = ModelUtil.fromJson(postResponse, User.class);

                // sign in
                AuthUtil.signIn(RegisterActivity.this, summoner, user, inView);
            } else { // display error
                String message = ScreenUtil.postResponseErrorMessage(RegisterActivity.this, postResponse);
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        }
    }

    private class ValidateDialog extends Dialog {

        final ReqRegister request;

        ValidateDialog(ReqRegister request) {
            super(RegisterActivity.this, R.style.AppTheme_Dialog);
            this.request = request;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_validate_ownership);
            setCancelable(false);

            // set code view
            TextView codeView = (TextView) findViewById(R.id.code_view);
            codeView.setText(request.code);

            // initialize buttons
            Button doneButton = (Button) findViewById(R.id.done_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RequestRegister().execute(request);
                    dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerButton.setEnabled(true);
                    dismiss();
                }
            });
        }
    }
}
