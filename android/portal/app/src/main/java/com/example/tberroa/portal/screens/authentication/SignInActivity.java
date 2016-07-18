package com.example.tberroa.portal.screens.authentication;

import android.R.layout;
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
import com.example.tberroa.portal.models.summoner.User;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.network.Http;

import java.io.IOException;

public class SignInActivity extends AppCompatActivity {

    private static final String RELOAD = "-80";
    private boolean inView;
    private String key;
    private EditText keyField;
    private String password;
    private EditText passwordField;
    private String region;
    private Spinner regionSelect;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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
        passwordField = (EditText) findViewById(R.id.password_field);
        regionSelect = (Spinner) findViewById(R.id.region_select_spinner);

        // initialize buttons
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                signInButton.setEnabled(false);
                signIn();
            }
        });
        TextView goToRegisterButton = (TextView) findViewById(R.id.go_to_register_view);
        goToRegisterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
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

    private void signIn() {
        key = keyField.getText().toString();
        password = passwordField.getText().toString();

        // make sure a region is selected
        int regionSelection = regionSelect.getSelectedItemPosition();
        if (regionSelection > 0) {
            region = AuthUtil.decodeRegion(regionSelection);
        } else { // display error
            Toast.makeText(this, getString(R.string.select_region), Toast.LENGTH_SHORT).show();
            signInButton.setEnabled(true);
            return;
        }

        new RequestSignIn().execute();
    }

    // makes sign in request to backend via http
    private class RequestSignIn extends AsyncTask<Void, Void, Void> {

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
                String url = "http://52.90.34.48/summoners/login.json";
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqSignIn.class));
            } catch (IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@SignInActivity: " + e.getMessage());
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
                AuthUtil.signIn(SignInActivity.this, summoner, user, inView);
            } else { // display error
                Toast.makeText(SignInActivity.this, postResponse, Toast.LENGTH_SHORT).show();
                signInButton.setEnabled(true);
            }
        }
    }
}