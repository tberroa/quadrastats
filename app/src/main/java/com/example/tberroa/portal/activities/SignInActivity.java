package com.example.tberroa.portal.activities;

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
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.helpers.AuthenticationUtil;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.network.NetworkUtil;

public class SignInActivity extends AppCompatActivity {

    private EditText summonerName, password;
    private Spinner region;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // no animation if coming from register activity
        if (getIntent().getAction() != null){
            overridePendingTransition(0, 0);
        }

        // check if summoner is already signed in
        if (new SummonerInfo().isSignedIn(this)){
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
        }

        // initialize summoner input fields
        summonerName = (EditText)findViewById(R.id.summoner_name);
        password = (EditText)findViewById(R.id.password);
        region = (Spinner)findViewById(R.id.region_spinner);

        // declare and initialize buttons
        signInButton = (Button)findViewById(R.id.sign_in);
        signInButton.setOnClickListener(signInButtonListener);
        TextView goToRegisterButton = (TextView)findViewById(R.id.register);
        goToRegisterButton.setOnClickListener(goToRegisterButtonListener);

        // set up region spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource
                (this, R.array.select_region, android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region.setAdapter(staticAdapter);
    }

    private final OnClickListener signInButtonListener = new OnClickListener() {
        public void onClick(View v) {
            signInButton.setEnabled(false);
            signIn();
        }
    };

    private final OnClickListener goToRegisterButtonListener = new OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(SignInActivity.this, RegisterActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Params.RELOAD));
            finish();
        }
    };

    // validation process
    private void signIn(){
        String enteredSummonerName = summonerName.getText().toString();
        String enteredPassword = password.getText().toString();

        Bundle enteredInfo = new Bundle();
        enteredInfo.putString("summoner_name", enteredSummonerName);
        enteredInfo.putString("password", enteredPassword);

        String response = AuthenticationUtil.validate(enteredInfo);
        if (response.matches("")){
            if (NetworkUtil.isInternetAvailable(this)){
                int regionSelection = region.getSelectedItemPosition();
                if (regionSelection > 0){
                    new AttemptSignIn().execute();
                }
                else{
                    Toast.makeText(this, getString(R.string.select_region), Toast.LENGTH_SHORT).show();
                    signInButton.setEnabled(true);
                }
            }
            else{
                signInButton.setEnabled(true);
                Toast.makeText(this, getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if (response.contains("summoner_name")){
                summonerName.setError(getResources().getString(R.string.summoner_name_format));
            }
            else{
                summonerName.setError(null);
            }
            if (response.contains("pass_word")){
                password.setError(getResources().getString(R.string.password_format));
            }
            else{
                password.setError(null);
            }
            signInButton.setEnabled(true);
        }
    }

    // attempts to sign in via http
    class AttemptSignIn extends AsyncTask<Void, Void, Void> {

        private String summonerName, password, region, keyValuePairs, postResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            summonerName = SignInActivity.this.summonerName.getText().toString();
            password = SignInActivity.this.password.getText().toString();
            region = AuthenticationUtil.decodeRegion(SignInActivity.this.region.getSelectedItemPosition());
            keyValuePairs = "app_name="+summonerName+"&password="+password+"&region="+region;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                String url = Params.SIGN_IN_URL;
                postResponse = new Http().post(url, keyValuePairs);
            } catch(java.io.IOException e){
                Log.e(Params.TAG_EXCEPTIONS,"@SignInActivity: " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void param) {
            if (postResponse.equals("success")) {
                // sign in
                Log.d(Params.TAG_DEBUG, "@SignInActivity: successful sign in");
                AuthenticationUtil.signIn(SignInActivity.this, summonerName, region);
            }
            else{ // display error
                Toast.makeText(SignInActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
            signInButton.setEnabled(true);
        }
    }
}