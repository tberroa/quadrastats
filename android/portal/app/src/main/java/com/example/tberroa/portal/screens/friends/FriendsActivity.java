package com.example.tberroa.portal.screens.friends;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqFriend;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.screens.home.HomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FriendsActivity extends BaseActivity {

    private boolean add;
    private List<Summoner> friends;
    private FriendsAdapter friendsAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.manage_friends);
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
        FloatingActionButton addFriend = (FloatingActionButton) findViewById(R.id.add_friend_button);
        addFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // construct and display a dialog containing a single text field where the user enters
                // the name of the summoner they want to add to their friends list
                final EditText friendKeyField = new EditText(FriendsActivity.this);
                friendKeyField.setSingleLine();

                Builder builder = new Builder(FriendsActivity.this);
                builder.setTitle(R.string.add_friend);
                builder.setView(friendKeyField);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.done, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String friendKey = friendKeyField.getText().toString();
                        add = true;
                        new RequestFriendOp().execute(friendKey);
                    }
                });
                builder.show();
            }
        });

        new ViewInitialization().execute();
    }

    // makes add/remove friend request to backend via http
    private class RequestFriendOp extends AsyncTask<String, Void, String[]> {

        Summoner friend;
        Summoner user;

        @Override
        protected String[] doInBackground(String... params) {
            LocalDB localDB = new LocalDB();

            // create the request object
            user = localDB.summoner(new UserInfo().getId(FriendsActivity.this));
            ReqFriend request = new ReqFriend();
            request.region = user.region;
            request.user_key = user.key;
            request.friend_key = params[0];

            // make the request
            String postResponse = "";
            try {
                String url;
                if (add) {
                    url = "http://52.90.34.48/summoners/add-friend.json";
                } else {
                    url = "http://52.90.34.48/summoners/remove-friend.json";
                }
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqFriend.class));
            } catch (IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@FriendsActivity: " + e.getMessage());
            }

            // a successful request requires further local database operations, do those here
            if (postResponse.contains("summoner_id") && add) {
                // save the new friend summoner object
                friend = ModelUtil.fromJson(postResponse, Summoner.class);
                friend.save();

                // add the new friend to the users local summoner object friend list
                user.addFriend(friend.key);
                user.save();
            } else if (postResponse.contains("summoner_id") && !add) {
                // get the users updated friend list from the returned object
                Summoner updatedUser = ModelUtil.fromJson(postResponse, Summoner.class);
                user.friends = updatedUser.friends;

                // profile icon might have updated too
                user.profile_icon = updatedUser.profile_icon;
                user.save();

                // delete the friend from local database
                localDB.summoner(request.friend_key).delete();
            }

            String[] values = new String[2];
            values[0] = params[0];
            values[1] = postResponse;
            return values;
        }

        @Override
        protected void onPostExecute(String[] values) {
            String friendKey = values[0];
            String postResponse = values[1];

            if (postResponse.contains("summoner_id")) {
                if (add) {
                    // update list view
                    friends.add(friend);
                    friendsAdapter.notifyDataSetChanged();

                    // make sure the no friends message is gone
                    TextView noFriends = (TextView) findViewById(R.id.no_friends_view);
                    noFriends.setVisibility(View.GONE);
                } else { // remove friend operation
                    // update list view
                    for (Iterator<Summoner> iterator = friends.listIterator(); iterator.hasNext(); ) {
                        String key = iterator.next().key;
                        if (key.equals(friendKey)) {
                            iterator.remove();
                        }
                    }
                    friendsAdapter.notifyDataSetChanged();

                    // check if friends list is empty
                    if (friends.isEmpty()) {
                        TextView noFriends = (TextView) findViewById(R.id.no_friends_view);
                        noFriends.setVisibility(View.VISIBLE);
                    }
                }
            } else { // display error
                Toast.makeText(FriendsActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ViewInitialization extends AsyncTask<Void, Void, List<Summoner>> {

        TextView noFriends;

        @Override
        protected List<Summoner> doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();

            // get user's friends
            Summoner user = localDB.summoner(new UserInfo().getId(FriendsActivity.this));
            List<String> keys = new ArrayList<>(Arrays.asList(user.friends.split(",")));

            return localDB.summoners(keys);
        }

        @Override
        protected void onPostExecute(final List<Summoner> friends) {
            FriendsActivity.this.friends = friends;

            // initialize list view
            ListView listView = (ListView) findViewById(R.id.list_view);
            friendsAdapter = new FriendsAdapter(FriendsActivity.this, friends);
            listView.setAdapter(friendsAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                    // construct and display a dialog asking the user if they want to remove the friend
                    Builder builder = new Builder(FriendsActivity.this);
                    builder.setTitle(R.string.remove_friend);
                    builder.setMessage(R.string.remove_friend_message);
                    builder.setCancelable(true);
                    builder.setPositiveButton(R.string.yes, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            add = false;
                            new RequestFriendOp().execute(friends.get(position).key);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            if (friends.isEmpty()) {
                // user has no friends
                noFriends.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            noFriends = (TextView) findViewById(R.id.no_friends_view);
            noFriends.setVisibility(View.GONE);
        }
    }
}
