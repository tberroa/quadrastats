package com.example.tberroa.portal.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private TextView goToSignInButton;
    private AlertDialog.Builder builder;
    private String enteredSummonerName;
    private boolean inView;

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
        goToSignInButton = (TextView)findViewById(R.id.go_to_sign_in);
        goToSignInButton.setOnClickListener(goToSignInButtonListener);

        // set up region spinner
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.select_region, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region.setAdapter(adapter);
    }

    private final View.OnClickListener registerButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            registerButton.setEnabled(false);
            Register();
        }
    };

    private final View.OnClickListener goToSignInButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Params.RELOAD);
            startActivity(intent);
            finish();
        }
    };

    // validation process
    private void Register(){
        enteredSummonerName = summonerName.getText().toString();
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

                    // validate summoner name in separate thread
                    new Thread(new Runnable() {
                        public void run() {
                            List<String> summonerList = new ArrayList<>();
                            summonerList.add(enteredSummonerName);
                            Map<String, SummonerDto> summoner;
                            summoner = new RiotAPI(RegisterActivity.this).getSummonersByName(summonerList);
                            Message msg = new Message();
                            if (summoner != null){
                                // save the stylized name
                                stylizedName = summoner.get(enteredSummonerName).name;

                                // construct dialog
                                constructDialog();

                                msg.arg1 = 2;
                            }
                            else{
                                msg.arg1 = 1;
                            }
                            handler.sendMessage(msg);
                        }
                    }).start();
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

    //method to verify summoner account ownership, called in dialog
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

    // dialog for validating ownership
    private void constructDialog(){
        // initialize validation key
        int key = new Random().nextInt(80000 - 65000) + 15000;
        final String keyString = Integer.toString(key);

        // construct text view to format message
        TextView dialogMessage = new TextView(RegisterActivity.this);
        dialogMessage.setPadding(15, 15, 15, 0);
        dialogMessage.setGravity(Gravity.CENTER);
        dialogMessage.setText(Html.fromHtml(
                "<h2>"+getString(R.string.validate_ownership_title)+"</h2>" +
                        "<p>"+getString(R.string.validate_ownership)+"</p>" +
                        keyString
        ));

        // construct dialog
        builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setView(dialogMessage);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                registerButton.setEnabled(true);
            }
        });
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // validate ownership in separate thread
                new Thread(new Runnable() {
                    public void run() {
                        Message msg = new Message();
                        msg.arg1 = (validOwnership(enteredSummonerName, keyString)) ? 4 : 3;
                        handler.sendMessage(msg);
                    }
                }).start();
                goToSignInButton.setEnabled(false);
                dialog.dismiss();
            }
        });
    }

    // handler used to respond to validation process which occurs in separate thread
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.arg1;
            switch (flag){
                case 4:
                    new AttemptRegister().execute();
                    break;
                case 3:
                    if (inView){
                        String toastMsg = getString(R.string.code_not_found);
                        Toast.makeText(RegisterActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    goToSignInButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    break;
                case 2:
                    if (inView){
                        builder.create().show();
                    }
                    break;
                case 1:
                    if (inView){
                        String toastMsg = getString(R.string.invalid_summoner_name);
                        Toast.makeText(RegisterActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    registerButton.setEnabled(true);
                    break;
            }
        }
    };

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
                AuthenticationUtil.signIn(RegisterActivity.this, summonerName, region, inView);
            }
            else{ // display error
                Toast.makeText(RegisterActivity.this, postResponse, Toast.LENGTH_SHORT).show();
                goToSignInButton.setEnabled(true);
                registerButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        inView = true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        inView = false;
    }
}
