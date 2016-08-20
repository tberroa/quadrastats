package com.quadrastats.screens;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.requests.ReqError;
import com.google.gson.JsonSyntaxException;

public class ScreenUtil {

    private ScreenUtil() {
    }

    public static int dpToPx(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }

    public static String postResponseErrorMessage(Context context, String postResponse) {
        if ("".equals(postResponse)) {
            return context.getString(R.string.err_network_error);
        }

        ReqError reqError;
        try {
            reqError = ModelUtil.fromJson(postResponse, ReqError.class);
        } catch (JsonSyntaxException e) {
            return context.getString(R.string.err_unknown);
        }

        if (reqError == null) {
            return context.getString(R.string.err_unknown);
        }

        switch (reqError.error) {
            case Constants.ERR_FRIEND_ALREADY_LISTED:
                return context.getString(R.string.err_friend_already_listed);
            case Constants.ERR_FRIEND_EQUALS_USER:
                return context.getString(R.string.err_friend_equals_user);
            case Constants.ERR_FRIEND_LIMIT_REACHED:
                return context.getString(R.string.err_friend_limit_reached);
            case Constants.ERR_INTERNAL_ERROR:
                return context.getString(R.string.err_internal_error);
            case Constants.ERR_INVALID_CREDENTIALS:
                return context.getString(R.string.err_invalid_credentials);
            case Constants.ERR_INVALID_REQUEST_FORMAT:
                return context.getString(R.string.err_invalid_request_format);
            case Constants.ERR_INVALID_RIOT_RESPONSE:
                return context.getString(R.string.err_invalid_riot_response);
            case Constants.ERR_SUMMONER_ALREADY_REGISTERED:
                return context.getString(R.string.err_summoner_already_registered);
            case Constants.ERR_SUMMONER_DOES_NOT_EXIST:
                return context.getString(R.string.err_summoner_does_not_exist);
            case Constants.ERR_SUMMONER_NOT_REGISTERED:
                return context.getString(R.string.err_summoner_not_registered);
            default:
                return reqError.message;
        }
    }

    public static String profileIconURL(String version, int iconId) {
        return Constants.URL_DATA_DRAGON + version + Constants.URL_PROFILE + iconId + Constants.URL_IMAGE_TYPE;
    }

    public static int screenHeight(Context context) {
        return screenDimensions(context).y;
    }

    public static int screenWidth(Context context) {
        return screenDimensions(context).x;
    }

    private static Point screenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenDimensions = new Point();
        display.getSize(screenDimensions);
        return screenDimensions;
    }
}
