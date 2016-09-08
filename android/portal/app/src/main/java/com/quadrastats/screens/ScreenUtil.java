package com.quadrastats.screens;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.requests.ReqError;
import com.quadrastats.network.HttpResponse;

public class ScreenUtil {

    private ScreenUtil() {
    }

    public static int dpToPx(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }

    public static String profileIconURL(String version, int iconId) {
        return Constants.URL_DATA_DRAGON + version + Constants.URL_PROFILE + iconId + Constants.URL_IMAGE_TYPE;
    }

    public static HttpResponse responseHandler(Context context, HttpResponse httpResponse) {
        // http connection never established
        if (httpResponse == null) {
            httpResponse = new HttpResponse();
            httpResponse.valid = false;
            httpResponse.error = context.getString(R.string.err_network_error);
            return httpResponse;
        }

        // faulty response code
        if (httpResponse.code != 200) {
            httpResponse.valid = false;
            httpResponse.error = httpResponse.body;
            return httpResponse;
        }

        // backend error occurred
        if (httpResponse.body.contains(Constants.VALID_ERROR)) {
            httpResponse.valid = false;
            ReqError reqError = ModelUtil.fromJson(httpResponse.body, ReqError.class);
            switch (reqError.error) {
                case Constants.ERR_FRIEND_ALREADY_LISTED:
                    httpResponse.error = context.getString(R.string.err_friend_already_listed);
                    return httpResponse;
                case Constants.ERR_FRIEND_EQUALS_USER:
                    httpResponse.error = context.getString(R.string.err_friend_equals_user);
                    return httpResponse;
                case Constants.ERR_FRIEND_LIMIT_REACHED:
                    httpResponse.error = context.getString(R.string.err_friend_limit_reached);
                    return httpResponse;
                case Constants.ERR_INTERNAL_ERROR:
                    httpResponse.error = context.getString(R.string.err_internal_error);
                    return httpResponse;
                case Constants.ERR_INVALID_CREDENTIALS:
                    httpResponse.error = context.getString(R.string.err_invalid_credentials);
                    return httpResponse;
                case Constants.ERR_INVALID_REQUEST_FORMAT:
                    httpResponse.error = context.getString(R.string.err_invalid_request_format);
                    return httpResponse;
                case Constants.ERR_INVALID_RIOT_RESPONSE:
                    httpResponse.error = context.getString(R.string.err_invalid_riot_response);
                    return httpResponse;
                case Constants.ERR_SUMMONER_ALREADY_REGISTERED:
                    httpResponse.error = context.getString(R.string.err_summoner_already_registered);
                    return httpResponse;
                case Constants.ERR_SUMMONER_DOES_NOT_EXIST:
                    httpResponse.error = context.getString(R.string.err_summoner_does_not_exist);
                    return httpResponse;
                case Constants.ERR_SUMMONER_NOT_REGISTERED:
                    httpResponse.error = context.getString(R.string.err_summoner_not_registered);
                    return httpResponse;
                default:
                    httpResponse.error = reqError.message;
                    return httpResponse;
            }
        }

        // no error occurred
        httpResponse.valid = true;
        return httpResponse;
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
