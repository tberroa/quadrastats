package com.example.tberroa.portal.activities;

// the signed in summoners profile

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.helpers.Utilities;

public class ProfileActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // initialize button
        Button signOutButton = (Button) findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(signOutButtonListener);

    }

    private final View.OnClickListener signOutButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Utilities.SignOut(ProfileActivity.this);
        }
    };
}
