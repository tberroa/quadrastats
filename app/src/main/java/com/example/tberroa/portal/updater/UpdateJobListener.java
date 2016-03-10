package com.example.tberroa.portal.updater;

/* Receiver which listens for the UPDATE_COMPLETE broadcast sent by the background update job. Upon update completion,
 * the receiver reloads the activity it lives in. */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.tberroa.portal.data.Params;

public class UpdateJobListener extends BroadcastReceiver {

    public IntentFilter getFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Params.UPDATE_COMPLETE);
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Params.UPDATE_COMPLETE:
                if (context instanceof Activity) {
                    Intent reload = ((Activity) context).getIntent();
                    reload.setAction(Params.RELOAD).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(reload);
                    ((Activity) context).finish();
                }
                break;
        }
    }
}
