package com.quadrastats.screens.authentication;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.data.UserData;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.requests.ReqSignIn;
import com.quadrastats.models.summoner.Summoner;
import com.quadrastats.network.Http;
import com.quadrastats.network.HttpResponse;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.home.HomeActivity;

import java.io.IOException;

public class SignInActivity extends AppCompatActivity {

    private boolean cancelled;
    private boolean inView;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // check if user is already signed in
        if (new UserData().isSignedIn(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        // get screen width
        int screenWidth = ScreenUtil.screenWidth(this);

        // resize layout according to screen
        int layoutWidth = (80 * screenWidth) / 100;
        LinearLayout signInLayout = (LinearLayout) findViewById(R.id.sign_in_layout);
        signInLayout.getLayoutParams().width = layoutWidth;
        signInLayout.setLayoutParams(signInLayout.getLayoutParams());

        // initialize loading spinner
        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingSpinner.getLayoutParams().width = (25 * screenWidth) / 100;
        loadingSpinner.getLayoutParams().height = (25 * screenWidth) / 100;
        loadingSpinner.setLayoutParams(loadingSpinner.getLayoutParams());
        loadingSpinner.setVisibility(View.GONE);

        // initialize input fields
        EditText keyField = (EditText) findViewById(R.id.summoner_name_field);
        Spinner regionSelect = (Spinner) findViewById(R.id.region_select_spinner);
        int color = ContextCompat.getColor(this, R.color.accent);
        regionSelect.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        // initialize buttons
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                signInButton.setEnabled(false);

                // get user inputs
                String key = keyField.getText().toString();

                // make sure a region is selected
                String region;
                int regionSelection = regionSelect.getSelectedItemPosition();
                if (regionSelection > 0) {
                    region = AuthUtil.decodeRegion(regionSelection);
                } else { // display error
                    String message = getString(R.string.err_select_region);
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                    signInButton.setEnabled(true);
                    return;
                }

                // create the request object
                ReqSignIn request = new ReqSignIn();
                request.region = region;
                request.key = key;

                // execute the request
                new RequestSignIn().execute(request);
            }
        });

        // populate region select spinner
        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.auth_select_region_array, R.layout.spinner_textview);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_textview);
        regionSelect.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelled = true;
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

    private class RequestSignIn extends AsyncTask<ReqSignIn, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(ReqSignIn... params) {
            // extract the request object
            ReqSignIn request = params[0];

            // make the request
            HttpResponse postResponse = null;
            try {
                String url = Constants.URL_SIGN_IN;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqSignIn.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // handle the response
            postResponse = ScreenUtil.responseHandler(SignInActivity.this, postResponse);

            return postResponse;
        }

        @Override
        protected void onPostExecute(HttpResponse postResponse) {
            // check if canceled
            if (cancelled) {
                return;
            }

            // turn loading spinner off
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.GONE);

            if (postResponse.valid) {
                // get the summoner object
                Summoner summoner = ModelUtil.fromJson(postResponse.body, Summoner.class);

                // sign in
                AuthUtil.signIn(SignInActivity.this, summoner, inView);
            } else { // display error
                Toast.makeText(SignInActivity.this, postResponse.error, Toast.LENGTH_SHORT).show();
                signInButton.setEnabled(true);
            }
        }

        @Override
        protected void onPreExecute() {
            // turn loading spinner on
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.VISIBLE);
        }
    }
}