package com.example.tberroa.portal.activities;

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
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.database.RiotAPI;
import com.example.tberroa.portal.helpers.AuthenticationUtil;
import com.example.tberroa.portal.models.summoner.RunePageDto;
import com.example.tberroa.portal.models.summoner.RunePagesDto;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.network.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity {

    final private SummonerInfo summonerInfo = new SummonerInfo();
    private EditText summonerName, password, confirmPassword;
    private String stylizedName;
    private Spinner region;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // no animation if coming from sign in activity
        if (getIntent().getAction() != null){
            overridePendingTransition(0, 0);
        }

        // check if summoner is already signed in
        if (summonerInfo.isSignedIn(this)){
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }

        // initialize summoner input fields
        summonerName = (EditText)findViewById(R.id.summoner_name);
        password = (EditText)findViewById(R.id.password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password);
        region = (Spinner)findViewById(R.id.region_spinner);

        // declare and initialize buttons
        registerButton = (Button)findViewById(R.id.register);
        registerButton.setOnClickListener(registerButtonListener);
        TextView goToSignInButton = (TextView)findViewById(R.id.go_to_sign_in);
        goToSignInButton.setOnClickListener(goToSignInButtonListener);

        // set up region spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource
                (this, R.array.select_region, android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region.setAdapter(staticAdapter);
    }

    private final View.OnClickListener registerButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            registerButton.setEnabled(false);
            Register();
        }
    };

    private final View.OnClickListener goToSignInButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(RegisterActivity.this, SignInActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Params.RELOAD));
            finish();
        }
    };

    // validation process
    private void Register(){
        final String enteredSummonerName = summonerName.getText().toString();
        final String enteredPassword = password.getText().toString();
        final String enteredConfirmPassword = confirmPassword.getText().toString();

        Bundle enteredInfo = new Bundle();
        enteredInfo.putString("summoner_name", enteredSummonerName);
        enteredInfo.putString("password", enteredPassword);
        enteredInfo.putString("confirm_password", enteredConfirmPassword);

        String response = AuthenticationUtil.validate(enteredInfo);
        if (response.matches("")){
            if (NetworkUtil.isInternetAvailable(this)){
                int regionSelection = region.getSelectedItemPosition();
                if (regionSelection > 0){
                    // decode region
                    String region = AuthenticationUtil.decodeRegion(regionSelection);
                    // save region
                    summonerInfo.setRegion(this, region);
                    // make sure its a valid summoner name
                    List<String> summonerList = new ArrayList<>();
                    summonerList.add(enteredSummonerName);
                    Map<String, SummonerDto> summoner = new RiotAPI(this).getSummonersByName(summonerList);
                    if (summoner != null){
                        // save the stylized name
                        stylizedName = summoner.get(enteredSummonerName).name;

                        // make sure this person owns that summoner account
                        // initialize validation key
                        int key = new Random().nextInt(80000 - 65000) + 15000;
                        final String keyString = Integer.toString(key);

                        // construct text view to format message
                        TextView dialogMessage = new TextView(this);
                        dialogMessage.setPadding(15, 15, 15, 0);
                        dialogMessage.setGravity(Gravity.CENTER);
                        dialogMessage.setText(Html.fromHtml(
                                "<h2>"+getString(R.string.validate_ownership_title)+"</h2>" +
                                "<p>"+getString(R.string.validate_ownership)+"</p>" +
                                keyString
                        ));

                        // construct and show dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setView(dialogMessage);
                        builder.setCancelable(false);
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){
                                registerButton.setEnabled(true);
                            }
                        });
                        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (validOwnership(enteredSummonerName, keyString)) {
                                    new AttemptRegister().execute();
                                } else {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.code_not_found), Toast.LENGTH_SHORT).show();
                                    registerButton.setEnabled(true);
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                    else{
                        Toast.makeText(this, getString(R.string.invalid_summoner_name), Toast.LENGTH_SHORT).show();
                        registerButton.setEnabled(true);
                    }
                }
                else{
                    Toast.makeText(this, getString(R.string.select_region), Toast.LENGTH_SHORT).show();
                    registerButton.setEnabled(true);
                }
            }
            else{
                Toast.makeText(this, getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
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
            registerButton.setEnabled(true);
        }
    }

    // part of validation process, method to verify summoner account ownership
    private boolean validOwnership(String summonerName, String keyString){
        RunePagesDto runePagesDto = new RiotAPI(this).getRunePages(summonerName);
        Set<RunePageDto> pages = runePagesDto.pages;
        Log.d(Params.TAG_DEBUG, "@validOwnership: keyString is " + keyString);
        for(RunePageDto page : pages){
            Log.d(Params.TAG_DEBUG, "@validOwnership: rune page name is " + page.name);
            if (page.name.equals(keyString)){
                return true;
            }
        }
        return false;
    }

    // attempts to register via http
    class AttemptRegister extends AsyncTask<Void, Void, Void> {

        private String summonerName, password, confirmPassword, region, keyValuePairs, postResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            summonerName = RegisterActivity.this.summonerName.getText().toString();
            password = RegisterActivity.this.password.getText().toString();
            confirmPassword = RegisterActivity.this.confirmPassword.getText().toString();
            region = AuthenticationUtil.decodeRegion(RegisterActivity.this.region.getSelectedItemPosition());
            keyValuePairs = "app_name="+summonerName+"&riot_name="+stylizedName+"&password="+password+
                            "&confirm_password="+confirmPassword+"&region="+region;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                String url = Params.REGISTER_URL;
                postResponse = new Http().post(url, keyValuePairs);
            } catch(java.io.IOException e){
                Log.e(Params.TAG_EXCEPTIONS,"@RegisterActivity: " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void param) {
            if (postResponse.contains("success")) {
                // sign in
                Log.d(Params.TAG_DEBUG, "@RegisterActivity: successful register");
                AuthenticationUtil.signIn(RegisterActivity.this, summonerName, region);
            }
            else{ // display error
                Toast.makeText(RegisterActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
            registerButton.setEnabled(true);
        }
    }
}
