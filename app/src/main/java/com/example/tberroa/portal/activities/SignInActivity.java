package com.example.tberroa.portal.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.helpers.Utilities;
import com.example.tberroa.portal.network.Http;

public class SignInActivity extends AppCompatActivity {

    private EditText summonerName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // check if user is already logged in
        //if (new UserInfo().isLoggedIn(this)){ // if so, send them to the client manager
         //   startActivity(new Intent(SignInActivity.this, ClientManagerActivity.class));
         //   finish();
        //}

        // initialize text boxes for user to enter their information
        summonerName = (EditText)findViewById(R.id.summoner_name);
        password = (EditText)findViewById(R.id.password);

        // allow user to submit form via keyboard
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    Login();
                    handled = true;
                }
                return handled;
            }
        });

        // declare and initialize buttons
        Button loginButton = (Button)findViewById(R.id.login);
        loginButton.setOnClickListener(loginButtonListener);
        TextView registerButton = (TextView)findViewById(R.id.register);
        registerButton.setOnClickListener(registerButtonListener);
    }

    private final OnClickListener loginButtonListener = new OnClickListener() {
        public void onClick(View v) {
            Login();
        }
    };

    private final OnClickListener registerButtonListener = new OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            finish();
        }
    };

    private void Login(){
        String enteredSummonerName = summonerName.getText().toString();
        String enteredPassword = password.getText().toString();

        Bundle enteredInfo = new Bundle();
        enteredInfo.putString("summoner_name", enteredSummonerName);
        enteredInfo.putString("password", enteredPassword);

        String response = Utilities.validate(enteredInfo);
        if (response.matches("")){
            new AttemptLogin().execute();
        }
        else{
            if (response.contains("summoner_name")){
                summonerName.setError(getResources().getString(R.string.summoner_name_format));
            }
            else{
                summonerName.setError(null);
            }
            if (response.contains("password")){
                password.setError(getResources().getString(R.string.password_format));
            }
            else{
                password.setError(null);
            }
        }
    }

    class AttemptLogin extends AsyncTask<Void, Void, Void> {

        private String summonerName, password, keyValuePairs, postResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            summonerName = SignInActivity.this.summonerName.getText().toString();
            password = SignInActivity.this.password.getText().toString();
            keyValuePairs = "&username="+summonerName+"&password="+password;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                String url = Params.LOGIN_URL;
                postResponse = new Http().post(url, keyValuePairs);
            } catch(java.io.IOException e){
                Log.e(Params.TAG_EXCEPTIONS, e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void param) {
            if (postResponse.equals("success")) {
                // sign in
                Utilities.SignIn(SignInActivity.this, summonerName);
            }
            else{ // display error
                Toast.makeText(SignInActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
        }
    }
}