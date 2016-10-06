package com.quadrastats.screens.friends;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.data.LocalDB;
import com.quadrastats.data.UserData;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.requests.ReqAddFriend;
import com.quadrastats.models.requests.ReqUpdate;
import com.quadrastats.models.summoner.Summoner;
import com.quadrastats.network.Http;
import com.quadrastats.network.HttpResponse;
import com.quadrastats.screens.BaseActivity;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.home.HomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FriendsActivity extends BaseActivity {

    private static double updateTime;
    private boolean busy;
    private boolean cancelled;
    private List<Summoner> friends;
    private FriendsAdapter friendsAdapter;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!busy) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
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
                    if (!busy) {
                        startActivity(new Intent(FriendsActivity.this, HomeActivity.class));
                        finish();
                    }
                }
            }
        });
        toolbar.setOnMenuItemClickListener(new MenuListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                super.onMenuItemClick(item);
                switch (item.getItemId()) {
                    case R.id.add_friend:
                        if (!busy) {
                            new AddFriendDialog().show();
                        }
                        break;
                    case R.id.update:
                        if (!busy) {
                            new UpdateDialog().show();
                        }
                        break;
                }
                return true;
            }
        });

        // initialize loading spinner
        int screenWidth = ScreenUtil.screenWidth(this);
        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingSpinner.getLayoutParams().width = (25 * screenWidth) / 100;
        loadingSpinner.getLayoutParams().height = (25 * screenWidth) / 100;
        loadingSpinner.setLayoutParams(loadingSpinner.getLayoutParams());
        loadingSpinner.setVisibility(View.GONE);

        new ViewInitialization().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelled = true;
    }

    private class AddFriend extends AsyncTask<String, Void, HttpResponse> {

        int error;
        Summoner friend;

        @Override
        protected HttpResponse doInBackground(String... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // get user
            Summoner user = localDB.summoner(userData.getId(FriendsActivity.this));

            // extract friend key
            String friendKey = params[0];

            // check if user is trying to add themselves
            if (friendKey.equals(user.key)) {
                error = 1;
                return null;
            }

            // check if this friend is already listed
            List<String> friends = Arrays.asList(user.friends.split(","));
            for (String friend : friends) {
                if (friend.equals(friendKey)) {
                    error = 2;
                    return null;
                }
            }

            // check if the friend limit has been reached
            if (friends.size() >= 20) {
                error = 3;
                return null;
            }

            // create the request object
            ReqAddFriend request = new ReqAddFriend();
            request.region = user.region;
            request.key = friendKey;

            // make the request
            HttpResponse postResponse = null;
            try {
                String url;
                url = Constants.URL_ADD_FRIEND;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqAddFriend.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // handle the response
            postResponse = ScreenUtil.responseHandler(FriendsActivity.this, postResponse);

            // a successful request requires further local database operations, do those here
            if (postResponse.valid) {
                // save the new friend summoner object
                friend = ModelUtil.fromJson(postResponse.body, Summoner.class);
                friend.save();

                // add the new friend to the users local summoner object friend list
                user.addFriend(friend.key);
                user.save();
            }

            return postResponse;
        }

        @Override
        protected void onPostExecute(HttpResponse postResponse) {
            // check if canceled
            if (cancelled) {
                return;
            }

            // check if error occurred
            if (error > 0) {
                String message = "";
                switch (error) {
                    case 1:
                        message = getString(R.string.mf_friend_equals_user);
                        break;
                    case 2:
                        message = getString(R.string.mf_friend_already_listed);
                        break;
                    case 3:
                        message = getString(R.string.mf_friend_limit_reached);
                        break;
                }
                Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();
            } else if (postResponse.valid) {
                // update list view
                friends.add(friend);
                friendsAdapter.notifyDataSetChanged();

                // make sure the no friends message is gone
                TextView noFriends = (TextView) findViewById(R.id.no_friends_view);
                noFriends.setVisibility(View.GONE);

                // display success
                String message = getString(R.string.mf_friend_added_success);
                Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();
            } else { // display error
                Toast.makeText(FriendsActivity.this, postResponse.error, Toast.LENGTH_SHORT).show();
            }

            // lower busy flag
            busy = false;

            // turn loading spinner off
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            // turn loading spinner on
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.VISIBLE);

            // set busy flag
            busy = true;
        }
    }

    private class AddFriendDialog extends Dialog {

        AddFriendDialog() {
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
                        new AddFriend().execute(friendKey);
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
                    new AddFriend().execute(friendKey);
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

    private class RemoveFriend extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // extract friend key
            String friendKey = params[0];

            // get the users summoner object
            Summoner user = localDB.summoner(userData.getId(FriendsActivity.this));

            // convert the users friend list into a list
            List<String> friends = new ArrayList<>(Arrays.asList(user.friends.split(",")));

            // delete the friend from the friends list
            for (Iterator<String> iterator = friends.listIterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                if (key.equals(friendKey)) {
                    iterator.remove();
                    break;
                }
            }

            // update the users summoner object
            user.friends = TextUtils.join(",", friends);
            user.save();

            // delete the friend from local database
            localDB.summoner(friendKey).delete();

            return friendKey;
        }

        @Override
        protected void onPostExecute(String friendKey) {
            // check if canceled
            if (cancelled) {
                return;
            }

            // update list view
            for (Iterator<Summoner> iterator = friends.listIterator(); iterator.hasNext(); ) {
                String key = iterator.next().key;
                if (key.equals(friendKey)) {
                    iterator.remove();
                    break;
                }
            }
            friendsAdapter.notifyDataSetChanged();

            // check if friends list is empty
            if (friends.size() == 1) {
                TextView noFriends = (TextView) findViewById(R.id.no_friends_view);
                noFriends.setVisibility(View.VISIBLE);
            }

            // display success
            String message = getString(R.string.mf_friend_removed_success);
            Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();

            // lower busy flag
            busy = false;

            // turn loading spinner off
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            // turn loading spinner on
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.VISIBLE);

            // set busy flag
            busy = true;
        }
    }

    private class RemoveFriendDialog extends Dialog {

        private final int position;

        RemoveFriendDialog(int position) {
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
                    new RemoveFriend().execute(friends.get(position).key);
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

    private class UpdateDialog extends Dialog {

        UpdateDialog() {
            super(FriendsActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_update);
            setCancelable(true);

            // initialize buttons
            Button yesButton = (Button) findViewById(R.id.yes_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpdateSummoners().execute();
                    dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            // initialize countdown view
            TextView countdownView = (TextView) findViewById(R.id.countdown_view);
            countdownView.setVisibility(View.GONE);

            // calculate the time elapsed since last update
            double timeElapsed = System.currentTimeMillis() - updateTime;

            // initialize message
            TextView messageView = (TextView) findViewById(R.id.update_message);
            String message;
            if (timeElapsed < Constants.UPDATE_FREQUENCY) {
                message = getString(R.string.mf_update_denied);
            } else {
                message = getString(R.string.mf_update_confirm);
            }
            messageView.setText(message);

            // set yes button availability
            if (timeElapsed < Constants.UPDATE_FREQUENCY) {
                yesButton.setEnabled(false);
            } else {
                yesButton.setEnabled(true);
            }

            // set countdown if required
            if (timeElapsed < Constants.UPDATE_FREQUENCY) {
                countdownView.setVisibility(View.VISIBLE);
                long timeLeft = (long) (Constants.UPDATE_FREQUENCY - timeElapsed);
                new CountDownTimer(timeLeft, 1000) {
                    public void onFinish() {
                        countdownView.setVisibility(View.GONE);
                        messageView.setText(getString(R.string.mf_update_confirm));
                        yesButton.setEnabled(true);
                    }

                    public void onTick(long millisUntilFinished) {
                        long seconds = (millisUntilFinished / 1000) % 60;
                        long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                        String secondsString = String.valueOf(seconds);
                        String minutesString = String.valueOf(minutes);
                        if (seconds < 10) {
                            secondsString = "0" + secondsString;
                        }
                        if (minutes < 10) {
                            minutesString = "0" + minutesString;
                        }
                        String viewText = minutesString + " : " + secondsString;
                        countdownView.setText(viewText);
                    }
                }.start();
            }
        }
    }

    private class UpdateSummoners extends AsyncTask<Void, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // get user
            Summoner user = localDB.summoner(userData.getId(FriendsActivity.this));

            // create list of keys
            List<String> keys = new ArrayList<>();
            keys.add(user.key);
            if (!"".equals(user.friends)) {
                keys.addAll(Arrays.asList(user.friends.split(",")));
            }

            // create request object
            ReqUpdate request = new ReqUpdate();
            request.region = user.region;
            request.keys = keys;

            // make the request
            HttpResponse postResponse = null;
            try {
                String url = Constants.URL_UPDATE;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqUpdate.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // handle the response
            postResponse = ScreenUtil.responseHandler(FriendsActivity.this, postResponse);

            return postResponse;
        }

        @Override
        protected void onPostExecute(HttpResponse postResponse) {
            // check if canceled
            if (cancelled) {
                return;
            }

            // update adapter
            friendsAdapter.notifyDataSetChanged();

            // check if an error occurred
            if (!postResponse.valid) {
                Toast.makeText(FriendsActivity.this, postResponse.error, Toast.LENGTH_SHORT).show();
            } else {
                // set update time
                updateTime = System.currentTimeMillis();

                // display success
                String message = getString(R.string.mf_update_success);
                Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            // check if user has no friends
            TextView noFriends = (TextView) findViewById(R.id.no_friends_view);
            if (friends.size() == 1) {
                noFriends.setVisibility(View.VISIBLE);
            } else {
                noFriends.setVisibility(View.GONE);
            }

            // lower busy flag
            busy = false;

            // turn loading spinner off
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            // turn loading spinner on
            ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.VISIBLE);

            // set busy flag
            busy = true;
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
            // check if canceled
            if (cancelled) {
                return;
            }

            // insert user to the beginning of the friends list
            friends.add(0, user);
            FriendsActivity.this.friends = friends;

            // initialize list view
            ListView listView = (ListView) findViewById(R.id.list_view);
            friendsAdapter = new FriendsAdapter(FriendsActivity.this, friends, staticRiotData.version);
            listView.setAdapter(friendsAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {
                    if (i > 0) {
                        if (!busy) {
                            new RemoveFriendDialog(i).show();
                        }
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
