package com.example.tberroa.portal.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.helpers.Utilities;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.network.NetworkUtil;

public class RegisterActivity extends AppCompatActivity {

    private EditText summonerName, password, confirmPassword, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // check if user is already signed in
        if (new UserInfo().isSignedIn(this)){
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }

        // initialize text boxes for user to enter their information
        summonerName = (EditText)findViewById(R.id.summoner_name);
        password = (EditText)findViewById(R.id.password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password);
        email = (EditText)findViewById(R.id.email);

        // allow user to submit form via keyboard
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    Register();
                    handled = true;
                }
                return handled;
            }
        });

        // declare and initialize buttons
        Button registerButton = (Button)findViewById(R.id.register);
        registerButton.setOnClickListener(registerButtonListener);
        TextView goToSignInButton = (TextView)findViewById(R.id.go_to_sign_in);
        goToSignInButton.setOnClickListener(goToSignInButtonListener);
    }

    private final View.OnClickListener registerButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Register();
        }
    };

    private final View.OnClickListener goToSignInButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
            finish();
        }
    };

    private void Register(){
        String enteredSummonerName = summonerName.getText().toString();
        String enteredPassword = password.getText().toString();
        String enteredConfirmPassword = confirmPassword.getText().toString();
        String enteredEmail = email.getText().toString();

        Bundle enteredInfo = new Bundle();
        enteredInfo.putString("summoner_name", enteredSummonerName);
        enteredInfo.putString("password", enteredPassword);
        enteredInfo.putString("confirm_password", enteredConfirmPassword);
        enteredInfo.putString("email", enteredEmail);

        String response = Utilities.validate(enteredInfo);
        if (response.matches("")){
            if (NetworkUtil.isInternetAvailable(this)){
                new AttemptRegister().execute();
            }
            else{
                Toast.makeText(this, "internet not available", Toast.LENGTH_SHORT).show();
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
            if (response.contains("confirm_password")){
                confirmPassword.setError(getResources().getString(R.string.password_mismatch));
            }
            else{
                confirmPassword.setError(null);
            }
            if (response.contains("email")){
                email.setError(getResources().getString(R.string.enter_valid_email));
            }
            else{
                email.setError(null);
            }
        }
    }

    class AttemptRegister extends AsyncTask<Void, Void, Void> {

        private String summonerName, password, confirmPassword, email, keyValuePairs, postResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            summonerName = RegisterActivity.this.summonerName.getText().toString();
            password = RegisterActivity.this.password.getText().toString();
            confirmPassword = RegisterActivity.this.confirmPassword.getText().toString();
            email = RegisterActivity.this.email.getText().toString();
            keyValuePairs = "username="+summonerName+"&password="+password+
                            "&confirmPassword="+confirmPassword+"&email="+email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                String url = Params.REGISTER_URL;
                postResponse = new Http().post(url, keyValuePairs);
            } catch(java.io.IOException e){
                Log.e(Params.TAG_EXCEPTIONS, e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void param) {
            if (postResponse.equals("success")) {
                // sign in
                Utilities.SignIn(RegisterActivity.this, summonerName);
            }
            else{ // display error
                Toast.makeText(RegisterActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
