package com.quadrastats.screens.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.data.LocalDB;
import com.quadrastats.data.UserData;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.summoner.Summoner;
import com.quadrastats.models.summoner.User;

import java.util.List;

public class AuthUtil {

    private AuthUtil() {
    }

    public static String decodeRegion(int position) {
        switch (position) {
            case 1:
                return Constants.REGION_BR;
            case 2:
                return Constants.REGION_EUNE;
            case 3:
                return Constants.REGION_EUW;
            case 4:
                return Constants.REGION_KR;
            case 5:
                return Constants.REGION_LAN;
            case 6:
                return Constants.REGION_LAS;
            case 7:
                return Constants.REGION_NA;
            case 8:
                return Constants.REGION_OCE;
            case 9:
                return Constants.REGION_RU;
            case 10:
                return Constants.REGION_TR;
            default:
                return "";
        }
    }

    public static boolean isNotValid(Context context, List<String> inputs, List<EditText> editTexts) {
        // initialize values
        boolean error = false;
        String name = null;
        String password = null;
        String email = null;
        String confirmPassword = null;
        EditText nameField = null;
        EditText passwordField = null;
        EditText emailField = null;
        EditText confirmPasswordField = null;

        // extract values
        int i = 0;
        for (String input : inputs) {
            switch (i) {
                case 0:
                    name = input;
                    break;
                case 1:
                    password = input;
                    break;
                case 2:
                    email = input;
                    break;
                case 3:
                    confirmPassword = input;
                    break;
            }
            i++;
        }
        i = 0;
        for (EditText editText : editTexts) {
            switch (i) {
                case 0:
                    nameField = editText;
                    break;
                case 1:
                    passwordField = editText;
                    break;
                case 2:
                    emailField = editText;
                    break;
                case 3:
                    confirmPasswordField = editText;
                    break;
            }
            i++;
        }

        // make sure name is not empty
        if ((name != null) && (nameField != null) && name.isEmpty()) {
            nameField.setError(context.getResources().getString(R.string.auth_empty_field));
            error = true;
        } else if (nameField != null) {
            nameField.setError(null);
        }

        // make sure password is not empty
        if ((password != null) && (passwordField != null) && password.isEmpty()) {
            passwordField.setError(context.getResources().getString(R.string.auth_empty_field));
            error = true;
        } else if (passwordField != null) {
            passwordField.setError(null);
        }

        // make sure email is not empty
        if ((email != null) && (emailField != null) && email.isEmpty()) {
            emailField.setError(context.getResources().getString(R.string.auth_empty_field));
            error = true;
        } else if (emailField != null) {
            emailField.setError(null);
        }

        // make sure passwords match
        if ((confirmPassword != null) && (confirmPasswordField != null) && !confirmPassword.equals(password)) {
            confirmPasswordField.setError(context.getResources().getString(R.string.auth_password_mismatch));
            error = true;
        } else if (confirmPasswordField != null) {
            confirmPasswordField.setError(null);
        }

        return error;
    }

    public static void signIn(Context context, Summoner summoner, User user, boolean inView) {
        new LocalDB().clearDatabase(context);
        new UserData().clear(context);

        // serialize the summoner and user objects
        String summonerJson = ModelUtil.toJson(summoner, Summoner.class);
        String userJson = ModelUtil.toJson(user, User.class);

        // create splash activity intent
        Intent splashActivity = new Intent(context, SplashActivity.class);
        splashActivity.putExtra("summoner", summonerJson);
        splashActivity.putExtra("user", userJson);
        splashActivity.putExtra("in_view", inView);

        // go to splash activity
        context.startActivity(splashActivity);

        // apply animation for entering splash activity
        ((Activity) context).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        // finish current activity
        ((Activity) context).finish();
    }

    public static void signOut(Context context) {
        new UserData().clear(context);
        new LocalDB().clearDatabase(context);

        // go to sign in page
        Intent signOutIntent = new Intent(context, SignInActivity.class);
        signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(signOutIntent);
    }
}
