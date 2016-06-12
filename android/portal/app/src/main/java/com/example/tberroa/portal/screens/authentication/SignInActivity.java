package com.example.tberroa.portal.screens.authentication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqSignIn;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.network.Http;

public class SignInActivity extends AppCompatActivity {

    private EditText keyField, passwordField;
    private String region, key, password;
    private Spinner regionSelect;
    private Button signInButton;
    private boolean inView;

    private final OnClickListener signInButtonListener = new OnClickListener() {
        public void onClick(View v) {
            signInButton.setEnabled(false);
            signIn();
        }
    };

    private final OnClickListener goToRegisterButtonListener = new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Params.RELOAD);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // no animation if starting activity as a reload
        if (getIntent().getAction() != null && getIntent().getAction().equals(Params.RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // check if user is already signed in
        if (new UserInfo().isSignedIn(this)) {
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
        }

        // initialize input fields
        keyField = (EditText) findViewById(R.id.key);
        passwordField = (EditText) findViewById(R.id.password);
        regionSelect = (Spinner) findViewById(R.id.region_select);

        // initialize buttons
        signInButton = (Button) findViewById(R.id.sign_in);
        signInButton.setOnClickListener(signInButtonListener);
        TextView goToRegisterButton = (TextView) findViewById(R.id.register);
        goToRegisterButton.setOnClickListener(goToRegisterButtonListener);

        // initialize region select spinner
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.select_region, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSelect.setAdapter(adapter);
    }

    private void signIn() {
        key = keyField.getText().toString();
        password = passwordField.getText().toString();

        // make sure a region is selected
        final int regionSelection = regionSelect.getSelectedItemPosition();
        if (regionSelection > 0) {
            region = AuthUtil.decodeRegion(regionSelection);
        } else { // display error
            Toast.makeText(this, getString(R.string.select_region), Toast.LENGTH_SHORT).show();
            signInButton.setEnabled(true);
            return;
        }

        new AttemptSignIn().execute();
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

    // makes sign in request to backend via http
    class AttemptSignIn extends AsyncTask<Void, Void, Void> {

        private String postResponse;

        @Override
        protected Void doInBackground(Void... params) {
            // create the request object
            ReqSignIn request = new ReqSignIn();
            request.region = region;
            request.key = key;
            request.password = password;

            // make the request
            try {
                String url = Params.BURL_SIGN_IN;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqSignIn.class));
            } catch (java.io.IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@SignInActivity: " + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void param) {
            if (!postResponse.contains("Error")) {
                // get the summoner object
                Summoner summoner = ModelUtil.fromJson(postResponse, Summoner.class);

                // sign in
                AuthUtil.signIn(SignInActivity.this, summoner, inView);
            } else { // display error
                Toast.makeText(SignInActivity.this, postResponse, Toast.LENGTH_SHORT).show();
                signInButton.setEnabled(true);
            }
        }
    }
}