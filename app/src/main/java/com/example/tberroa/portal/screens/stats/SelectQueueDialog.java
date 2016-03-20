package com.example.tberroa.portal.screens.stats;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.screens.stats.recent.RecentActivity;

public class SelectQueueDialog extends Dialog {

    private final Context context;

    public SelectQueueDialog(Context context){
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.element_select_queue);

        // initialize options
        TextView dynamicQueue = (TextView) findViewById(R.id.dynamic_queue);
        TextView soloQueue = (TextView) findViewById(R.id.solo_queue);
        TextView team3 = (TextView) findViewById(R.id.team_3);

        // set on click listeners
        final Intent intent = new Intent(context, RecentActivity.class);
        dynamicQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent.putExtra("queue", Params.DYNAMIC_QUEUE));
                ((Activity) context).finish();
            }
        });

        soloQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent.putExtra("queue", Params.SOLO_QUEUE));
                ((Activity) context).finish();
            }
        });

        team3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent.putExtra("queue", Params.TEAM_3));
                ((Activity) context).finish();
            }
        });
    }


}
