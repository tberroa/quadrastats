package com.example.tberroa.portal.screens.friends;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserData;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqFriend;
import com.example.tberroa.portal.models.requests.ReqGetSummoners;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FriendsActivity extends BaseActivity implements OnRefreshListener {

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
        toolbar.setTitle(R.string.mf_activity_title);
        toolbar.inflateMenu(R.menu.friends_menu);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_button));
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
        toolbar.setOnMenuItemClickListener(new MenuListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                super.onMenuItemClick(item);
                switch (item.getItemId()) {
                    case R.id.add_friend:
                        new AddFriendDialog().show();
                }
                return true;
            }
        });

        // initialize swipe layout
        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        new ViewInitialization().execute();
    }

    @Override
    public void onRefresh() {
        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setRefreshing(true);
        new UpdateSummoners().execute();
    }

    private class AddFriendDialog extends Dialog {

        public AddFriendDialog() {
            super(FriendsActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_add_friend);
            setCancelable(true);

            // initialize input field
            EditText friendKeyField = (EditText) findViewById(R.id.friend_name_field);
            friendKeyField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String friendKey = friendKeyField.getText().toString();
                        add = true;
                        new RequestFriendOp().execute(friendKey);
                        dismiss();
                    }
                    return false;
                }
            });

            // initialize buttons
            Button doneButton = (Button) findViewById(R.id.done_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String friendKey = friendKeyField.getText().toString();
                    add = true;
                    new RequestFriendOp().execute(friendKey);
                    dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    private class RemoveFriendDialog extends Dialog {

        private final int position;

        public RemoveFriendDialog(int position) {
            super(FriendsActivity.this, R.style.AppTheme_Dialog);
            this.position = position;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_remove_friend);
            setCancelable(true);

            // initialize buttons
            Button yesButton = (Button) findViewById(R.id.yes_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add = false;
                    new RequestFriendOp().execute(friends.get(position).key);
                    dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    private class RequestFriendOp extends AsyncTask<String, Void, String[]> {

        Summoner friend;
        Summoner user;

        @Override
        protected String[] doInBackground(String... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // create the request object
            user = localDB.summoner(userData.getId(FriendsActivity.this));
            ReqFriend request = new ReqFriend();
            request.region = user.region;
            request.user_key = user.key;
            request.friend_key = params[0];

            // make the request
            String postResponse = "";
            try {
                String url;
                if (add) {
                    url = Constants.URL_ADD_FRIEND;
                } else {
                    url = Constants.URL_REMOVE_FRIEND;
                }
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqFriend.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // a successful request requires further local database operations, do those here
            if (postResponse.contains(Constants.VALID_FRIEND_OP) && add) {
                // save the new friend summoner object
                friend = ModelUtil.fromJson(postResponse, Summoner.class);
                friend.save();

                // add the new friend to the users local summoner object friend list
                user.addFriend(friend.key);
                user.save();
            } else if (postResponse.contains(Constants.VALID_FRIEND_OP) && !add) {
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

            if (postResponse.contains(Constants.VALID_FRIEND_OP)) {
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
                    if (friends.size() == 1) {
                        TextView noFriends = (TextView) findViewById(R.id.no_friends_view);
                        noFriends.setVisibility(View.VISIBLE);
                    }
                }
            } else { // display error
                String message = ScreenUtil.postResponseErrorMessage(FriendsActivity.this, postResponse);
                Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateSummoners extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // get user
            Summoner user = localDB.summoner(userData.getId(FriendsActivity.this));

            // create request object for getting the users updated summoner object from the server
            ReqGetSummoners requestUpdatedUser = new ReqGetSummoners();
            requestUpdatedUser.region = user.region;
            requestUpdatedUser.keys = new ArrayList<>();
            requestUpdatedUser.keys.add(user.key);

            // make the request
            String postResponse1 = "";
            try {
                String url = Constants.URL_GET_SUMMONERS;
                postResponse1 = new Http().post(url, ModelUtil.toJson(requestUpdatedUser, ReqGetSummoners.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // update the original user object
            if (postResponse1.contains(Constants.VALID_GET_SUMMONERS)) {
                Type type = new TypeToken<List<Summoner>>() {
                }.getType();
                List<Summoner> updatedUserList = ModelUtil.fromJsonList(postResponse1, type);
                Summoner updatedUser = updatedUserList.get(0);
                user.tier = updatedUser.tier;
                user.division = updatedUser.division;
                user.lp = updatedUser.lp;
                user.wins = updatedUser.wins;
                user.losses = updatedUser.losses;
                user.series = updatedUser.series;
                user.profile_icon = updatedUser.profile_icon;
                user.friends = updatedUser.friends;
                user.save();
            } else {
                return postResponse1;
            }

            // check if the updated user has no friends
            if ("".equals(user.friends)) {
                // empty the friends list
                ActiveAndroid.beginTransaction();
                try {
                    for (ListIterator<Summoner> iFriend = friends.listIterator(); iFriend.hasNext(); ) {
                        int i = iFriend.nextIndex();
                        Summoner friend = iFriend.next();
                        if (i != 0) {
                            friend.delete();
                            iFriend.remove();
                        }
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return Constants.VALID_GET_SUMMONERS;
            }

            // use the updated user to create request object for getting the friends updated objects from the server
            ReqGetSummoners request = new ReqGetSummoners();
            request.region = user.region;
            request.keys = new ArrayList<>(Arrays.asList(user.friends.split(",")));

            // make the request
            String postResponse2 = "";
            try {
                String url = Constants.URL_GET_SUMMONERS;
                postResponse2 = new Http().post(url, ModelUtil.toJson(request, ReqGetSummoners.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // update the friend summoner objects
            if (postResponse2.contains(Constants.VALID_GET_SUMMONERS)) {
                Type type = new TypeToken<List<Summoner>>() {
                }.getType();
                List<Summoner> updatedFriends = ModelUtil.fromJsonList(postResponse2, type);
                ActiveAndroid.beginTransaction();
                try {
                    // update the original friends that are on the new list and delete any not on the new list
                    for (ListIterator<Summoner> iFriend = friends.listIterator(); iFriend.hasNext(); ) {
                        int i = iFriend.nextIndex();
                        Summoner friend = iFriend.next();
                        if (i != 0) {
                            boolean found = false;
                            for (Iterator<Summoner> iUFriend = updatedFriends.listIterator(); iUFriend.hasNext(); ) {
                                Summoner updatedFriend = iUFriend.next();
                                if (friend.key.equals(updatedFriend.key)) {
                                    found = true;
                                    friend.tier = updatedFriend.tier;
                                    friend.division = updatedFriend.division;
                                    friend.lp = updatedFriend.lp;
                                    friend.wins = updatedFriend.wins;
                                    friend.losses = updatedFriend.losses;
                                    friend.series = updatedFriend.series;
                                    friend.profile_icon = updatedFriend.profile_icon;
                                    friend.save();
                                    iUFriend.remove();
                                    break;
                                }
                            }
                            if (!found) {
                                friend.delete();
                                iFriend.remove();
                            }
                        }
                    }

                    // if the new list has new friends, save them locally and add to list of friends for adapter
                    for (Summoner updatedFriend : updatedFriends) {
                        updatedFriend.save();
                        friends.add(updatedFriend);
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
            }

            return postResponse2;
        }

        @Override
        protected void onPostExecute(String postResponse) {
            // turn off refresh animation
            SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
            swipeLayout.setRefreshing(false);

            // update adapter
            friendsAdapter.notifyDataSetChanged();

            // check if an error occurred
            if (!postResponse.contains(Constants.VALID_GET_SUMMONERS)) {
                String message = ScreenUtil.postResponseErrorMessage(FriendsActivity.this, postResponse);
                Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            // check if user has no friends
            TextView noFriends = (TextView) findViewById(R.id.no_friends_view);
            if (friends.size() == 1) {
                noFriends.setVisibility(View.VISIBLE);
            } else {
                noFriends.setVisibility(View.GONE);
            }
        }
    }

    private class ViewInitialization extends AsyncTask<Void, Void, List<Summoner>> {

        TextView noFriends;
        Summoner user;

        @Override
        protected List<Summoner> doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // get user's friends
            user = localDB.summoner(userData.getId(FriendsActivity.this));
            List<String> keys = new ArrayList<>(Arrays.asList(user.friends.split(",")));

            return localDB.summoners(keys);
        }

        @Override
        protected void onPostExecute(List<Summoner> friends) {
            // insert user to the beginning of the friends list
            friends.add(0, user);
            FriendsActivity.this.friends = friends;

            // initialize list view
            ListView listView = (ListView) findViewById(R.id.list_view);
            friendsAdapter = new FriendsAdapter(FriendsActivity.this, friends, staticRiotData.version);
            listView.setAdapter(friendsAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    if (position > 0) {
                        new RemoveFriendDialog(position).show();
                    }
                }
            });

            if (friends.size() == 1) {
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
