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
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.summoner.FriendsList;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.screens.authentication.AuthUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.NetworkUtil;
import com.example.tberroa.portal.updater.UpdateUtil;

import java.util.ArrayList;
import java.util.Map;

public class FriendsActivity extends BaseActivity {

    FriendsList friendsList;
    private FloatingActionButton addFriend;
    private boolean inView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // no animation if starting activity as a reload
        if (getIntent().getAction() != null && getIntent().getAction().equals(Params.RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // initialize toolbar
        if (getSupportActionBar() != null) {
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
                } else {
                    startActivity(new Intent(FriendsActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // initialize add friend button
        addFriend = (FloatingActionButton) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(addFriendListener);

        // get friends dto
        friendsList = new LocalDB().getFriendsList();

        if (friendsList != null && !friendsList.getFriends().isEmpty()) {
            // set list view
            ListView listView = (ListView) findViewById(R.id.list_view);
            FriendsAdapter friendsAdapter = new FriendsAdapter(this, friendsList.getFriends());
            listView.setAdapter(friendsAdapter);
        } else {
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
                    // validate user input in a separate thread
                    new Thread(new Runnable() {
                        public void run() {
                            Message msg = new Message();

                            // check if the user has hit the friend limit
                            FriendsList friendsList = new LocalDB().getFriendsList();
                            if (friendsList != null && friendsList.getFriends().size() == Params.MAX_FRIENDS) {
                                msg.arg1 = 1;
                                handler.sendMessage(msg);
                                return;
                            }

                            // check if internet is not available
                            if (!NetworkUtil.isInternetAvailable(FriendsActivity.this)) {
                                msg.arg1 = 2;
                                handler.sendMessage(msg);
                                return;
                            }

                            // query riot for the summoner dto corresponding to the entered name
                            String enteredName = friendsName.getText().toString().toLowerCase();
                            Map<String, SummonerDto> friendDtoMap;
                            friendDtoMap = AuthUtil.validateName(FriendsActivity.this, enteredName);

                            // check if the entered name was a valid summoner name
                            if (friendDtoMap == null) {
                                msg.arg1 = 3;
                                handler.sendMessage(msg);
                                return;
                            }

                            // initialize the friend dto and get the stylized name of the friend
                            SummonerDto friendDto = new SummonerDto();
                            String name = "";
                            for (Map.Entry<String, SummonerDto> friend : friendDtoMap.entrySet()) {
                                friendDto = friend.getValue();
                                name = friend.getValue().name;
                            }

                            // check if the user entered themselves as a friend
                            if (name.equals(new UserInfo().getStylizedName(FriendsActivity.this))) {
                                msg.arg1 = 4;
                                handler.sendMessage(msg);
                                return;
                            }

                            // check if this friend has already been entered
                            if (friendsList != null){
                                for (SummonerDto friend : friendsList.getFriends()){
                                    if (friend != null && name.equals(friend.name)){
                                        msg.arg1 = 5;
                                        handler.sendMessage(msg);
                                        return;
                                    }
                                }
                            }

                            // if all checks were passed successfully, add the friend and save them locally
                            UpdateUtil.addPlayerToProfileMap(FriendsActivity.this, name);
                            if (friendsList == null) { // first friend
                                friendsList = new FriendsList();
                            }
                            friendsList.friends = new ArrayList<>();
                            friendsList.friends.add(friendDto);
                            friendsList.cascadeSave();

                            msg.arg1 = 6;
                            handler.sendMessage(msg);
                        }
                    }).start();
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
            switch (flag) {
                case 6: // all checks passed, reload activity
                    if (inView) {
                        Intent intent = new Intent(FriendsActivity.this, FriendsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).setAction(Params.RELOAD);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 5: // this friend already exists
                    if (inView) {
                        String toastMsg = getString(R.string.friend_already_exists);
                        Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4: // user entered themselves as a friend
                    if (inView) {
                        String toastMsg = getString(R.string.cant_add_yourself_as_friend);
                        Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3: // entered name is invalid
                    if (inView) {
                        String toastMsg = getString(R.string.invalid_summoner_name);
                        Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2: // internet not available
                    if (inView) {
                        String toastMsg = getString(R.string.internet_not_available);
                        Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1: // friend limit reached
                    if (inView) {
                        String toastMsg = getString(R.string.friend_limit_reached);
                        Toast.makeText(FriendsActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            addFriend.setEnabled(true);
        }
    };

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
