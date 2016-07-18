package com.example.tberroa.portal.screens.authentication;

import android.R.layout;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import com.example.tberroa.portal.models.summoner.User;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.network.Http;

import java.io.IOException;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private static final String RELOAD = "-80";
    private String codeString;
    private EditText confirmPasswordField;
    private String email;
    private EditText emailField;
    private boolean inView;
    private String key;
    private EditText keyField;
    private String password;
    private EditText passwordField;
    private String region;
    private Spinner regionSelect;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // no animation if starting activity as a reload
        if ((getIntent().getAction() != null) && getIntent().getAction().equals(RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // check if user is already signed in
        if (new UserInfo().getSignInStatus(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        // initialize input fields
        keyField = (EditText) findViewById(R.id.summoner_name_field);
        emailField = (EditText) findViewById(R.id.email_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        confirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);
        regionSelect = (Spinner) findViewById(R.id.region_select_spinner);

        // initialize buttons
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerButton.setEnabled(false);
                register();
            }
        });
        TextView goToSignInButton = (TextView) findViewById(R.id.go_to_sign_in_view);
        goToSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(RELOAD);
                startActivity(intent);
                finish();
            }
        });

        // initialize region select spinner
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.auth_select_region, layout.simple_spinner_item);
        adapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
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

    private void register() {
        key = keyField.getText().toString();
        email = emailField.getText().toString();
        password = passwordField.getText().toString();
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
        int regionSelection = regionSelect.getSelectedItemPosition();
        if (regionSelection > 0) {
            region = AuthUtil.decodeRegion(regionSelection);
        } else { // display error
            Toast.makeText(this, getString(R.string.select_region), Toast.LENGTH_SHORT).show();
            registerButton.setEnabled(true);
            return;
        }

        // initialize validation code
        int code = new Random().nextInt(80000 - 65000) + 15000;
        codeString = Integer.toString(code);

        // format instructions regarding ownership validation within a text view
        TextView dialogMessage = new TextView(this);
        dialogMessage.setPadding(15, 15, 15, 0);
        dialogMessage.setGravity(Gravity.CENTER);
        dialogMessage.setText(Html.fromHtml(
                "<h2>" + getString(R.string.auth_validate_ownership) + "</h2>" +
                        "<p>" + getString(R.string.auth_validate_ownership_instructions) + "</p>" +
                        codeString
        ));

        // construct dialog
        Builder builder = new Builder(this);
        builder.setView(dialogMessage);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.button_cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                registerButton.setEnabled(true);
            }
        });
        builder.setPositiveButton(R.string.button_done, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new RequestRegister().execute();
                dialog.dismiss();
            }
        });

        // display dialog
        builder.create().show();
    }

    // makes registration request to backend via http
    private class RequestRegister extends AsyncTask<Void, Void, Void> {

        private String postResponse;

        @Override
        protected Void doInBackground(Void... params) {
            // create the request object
            ReqRegister request = new ReqRegister();
            request.email = email;
            request.password = password;
            request.region = region;
            request.key = key;
            request.code = codeString;

            // make the request
            try {
                String url = "http://52.90.34.48/summoners/register.json";
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqRegister.class));
            } catch (IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@RegisterActivity: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (postResponse.contains("summoner_id")) {
                // get the summoner object
                Summoner summoner = ModelUtil.fromJson(postResponse, Summoner.class);

                // get the user object
                User user = ModelUtil.fromJson(postResponse, User.class);

                // sign in
                AuthUtil.signIn(RegisterActivity.this, summoner, user, inView);
            } else { // display error
                Toast.makeText(RegisterActivity.this, postResponse, Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            }
        }
    }

}
