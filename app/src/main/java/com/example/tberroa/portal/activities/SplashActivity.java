package com.example.tberroa.portal.activities;

// Intermediate activity which occurs right after signing in. It's purpose
// is to allow for the update service to get a head start.

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.database.RiotAPI;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.services.UpdateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private boolean inView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // start update service
        startService(new Intent(this, UpdateService.class));

        new Thread(new Runnable() {
            @Override
            public void run() {
                // save stylized summoner name
                RiotAPI riotAPI = new RiotAPI(SplashActivity.this);
                SummonerInfo summonerInfo = new SummonerInfo();
                String basicName = summonerInfo.getBasicName(SplashActivity.this);
                List<String> nameList = new ArrayList<>();
                nameList.add(basicName);
                Map<String, SummonerDto> summonerMap = riotAPI.getSummonersByName(nameList);
                if (summonerMap != null){
                    summonerInfo.setStylizedName(SplashActivity.this, summonerMap.get(basicName).name);
                }
            }
        }).start();

        // go to activity_profile activity after 3 seconds have passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (inView){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 3000);
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
