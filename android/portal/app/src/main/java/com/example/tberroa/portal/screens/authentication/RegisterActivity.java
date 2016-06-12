package com.example.tberroa.portal.screens.authentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqRegister;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.network.Http;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    final private UserInfo userInfo = new UserInfo();
    private EditText keyField, passwordField, confirmPasswordField;
    private String region, key, password, codeString;
    private Spinner regionSelect;
    private Button registerButton;
    private boolean inView;

    private final View.OnClickListener goToSignInButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Params.RELOAD);
            startActivity(intent);
            finish();
        }
    };

    private final View.OnClickListener registerButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            registerButton.setEnabled(false);
            Register();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // no animation if starting activity as a reload
        if (getIntent().getAction() != null && getIntent().getAction().equals(Params.RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // check if user is already signed in
        if (userInfo.isSignedIn(this)) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }

        // initialize input fields
        keyField = (EditText) findViewById(R.id.key);
        passwordField = (EditText) findViewById(R.id.password);
        confirmPasswordField = (EditText) findViewById(R.id.confirm_password);
        regionSelect = (Spinner) findViewById(R.id.region_select);

        // initialize buttons
        registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(registerButtonListener);
        TextView goToSignInButton = (TextView) findViewById(R.id.go_to_sign_in);
        goToSignInButton.setOnClickListener(goToSignInButtonListener);

        // initialize region select spinner
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.select_region, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSelect.setAdapter(adapter);
    }

    private void Register() {
        key = keyField.getText().toString();
        password = passwordField.getText().toString();
        final String confirmPassword = confirmPasswordField.getText().toString();

        // make sure passwords match
        if (confirmPassword.equals(password)) {
            confirmPasswordField.setError(null);
        } else {
            confirmPasswordField.setError(getResources().getString(R.string.password_mismatch));
            registerButton.setEnabled(true);
            return;
        }

        // make sure a region is selected
        final int regionSelection = regionSelect.getSelectedItemPosition();
        if (regionSelection > 0) {
            region = AuthUtil.decodeRegion(regionSelection);
        } else {
            Toast.makeText(this, getString(R.string.select_region), Toast.LENGTH_SHORT).show();
            registerButton.setEnabled(true);
            return;
        }

        // initialize validation code
        final int code = new Random().nextInt(80000 - 65000) + 15000;
        codeString = Integer.toString(code);

        // format instructions regarding ownership validation within a text view
        TextView dialogMessage = new TextView(RegisterActivity.this);
        dialogMessage.setPadding(15, 15, 15, 0);
        dialogMessage.setGravity(Gravity.CENTER);
        dialogMessage.setText(Html.fromHtml(
                "<h2>" + getString(R.string.validate_ownership_title) + "</h2>" +
                        "<p>" + getString(R.string.validate_ownership) + "</p>" +
                        codeString
        ));

        // construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setView(dialogMessage);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                registerButton.setEnabled(true);
            }
        });
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new AttemptRegister().execute();
                dialog.dismiss();
            }
        });

        // display dialog
        builder.create().show();
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

    // makes registration request to backend via http
    class AttemptRegister extends AsyncTask<Void, Void, Void> {

        private String postResponse;

        @Override
        protected Void doInBackground(Void... params) {
            // create the request object
            ReqRegister request = new ReqRegister();
            request.user.password = password;
            request.region = region;
            request.key = key;
            request.code = codeString;

            // make the request
            try {
                String url = Params.BURL_REGISTER;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqRegister.class));
            } catch (java.io.IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@RegisterActivity: " + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void param) {
            if (!postResponse.contains("error")) {
                // get the summoner object
                Summoner summoner = ModelUtil.fromJson(postResponse, Summoner.class);

                // sign in
                AuthUtil.signIn(RegisterActivity.this, summoner, inView);
            } else { // display error
                Toast.makeText(RegisterActivity.this, postResponse, Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        }
    }
}
