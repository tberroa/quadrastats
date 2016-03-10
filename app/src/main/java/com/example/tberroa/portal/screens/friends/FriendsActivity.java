package com.example.tberroa.portal.screens.friends;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.screens.authentication.AuthUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.NetworkUtil;

import java.util.Map;
import java.util.Set;

public class FriendsActivity extends BaseActivity {

    private FloatingActionButton addFriend;
    private boolean inView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // no animation if starting activity as a reload
        if (getIntent().getAction() != null && getIntent().getAction().equals(Params.RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // initialize toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.friends);
        }

        // initialize back button
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.back_button));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    startActivity(new Intent(FriendsActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // initialize add friend button
        addFriend = (FloatingActionButton) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(addFriendListener);

        // get friends
        Set<String> friends = new FriendsInfo().getNames(this);

        if (friends.size() > 0){
            // initialize names
            String[] names = new String[friends.size()];
            int i = 0;
            for (String friendId : friends){
                names[i] = friendId;
                i++;
            }

            // set list view
            ListView listView = (ListView) findViewById(R.id.list_view);
            FriendsAdapter friendsAdapter = new FriendsAdapter(this, names);
            listView.setAdapter(friendsAdapter);
        }
        else {
            TextView noFriends = (TextView) findViewById(R.id.no_friends);
            noFriends.setVisibility(View.VISIBLE);
        }
    }

    private final View.OnClickListener addFriendListener = new View.OnClickListener() {
        public void onClick(View v) {
            addFriend.setEnabled(false);

            final EditText friendsName = new EditText(FriendsActivity.this);
            friendsName.setSingleLine();

            AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
            builder.setTitle(R.string.add_friend);
            builder.setView(friendsName);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (NetworkUtil.isInternetAvailable(FriendsActivity.this)) {
                        // validate summoner name in separate thread
                        new Thread(new Runnable() {
                            public void run() {
                                String enteredName = friendsName.getText().toString().toLowerCase();

                                // validate summoner name
                                Map<String, SummonerDto> summoner;
                                summoner = AuthUtil.validateName(FriendsActivity.this, enteredName);
                                Message msg = new Message();
                                if (summoner != null) {
                                    // save friend dto
                                    summoner.get(enteredName).save();

                                    // save name
                                    new FriendsInfo().addFriend(FriendsActivity.this, summoner.get(enteredName).name);

                                    msg.arg1 = 2;

                                } else {
                                    msg.arg1 = 1;

                                }
                                handler.sendMessage(msg);
                            }
                        }).start();

                    } else {
                        String toastMsg = getString(R.string.internet_not_available);
                        Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    addFriend.setEnabled(true);
                }
            });
            builder.show();
        }
    };

    // handler used to respond to summoner name validation which occurs in separate thread
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.arg1;
            switch (flag){
                case 2:
                    if (inView){
                        Intent intent = new Intent(FriendsActivity.this, FriendsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Params.RELOAD);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 1:
                    if (inView){
                        String toastMsg = getString(R.string.invalid_summoner_name);
                        Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            addFriend.setEnabled(true);
        }
    };

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
