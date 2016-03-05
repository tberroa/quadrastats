package com.example.tberroa.portal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.helpers.ScreenUtil;
import com.squareup.picasso.Picasso;

public class HomeAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] labels;

    public HomeAdapter(Context context, String[] labels) {
        super(context, -1, labels);
        this.context = context;
        this.labels = labels;
    }

    class ViewHolder {
        ImageView thumbnail;
        TextView label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null){
            // initialize view holder and layout inflater
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.home_list_row, parent, false);

            // initialize views
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            viewHolder.label = (TextView) convertView.findViewById(R.id.label);

            // set tag
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set label
        viewHolder.label.setText(labels[position]);

        // get screen dimensions
        int width = ScreenUtil.getScreenWidth(context);
        int height = ScreenUtil.getScreenHeight(context) / 4;

        // set image
        switch(position){
            case 0:
                Picasso.with(context).load(R.drawable.splash_zed).centerCrop().resize(width, height).into(viewHolder.thumbnail);
                break;
            case 1:
                Picasso.with(context).load(R.drawable.splash_amumu).centerCrop().resize(width, height).into(viewHolder.thumbnail);
                break;
            case 2:
                Picasso.with(context).load(R.drawable.splash_shyvana).centerCrop().resize(width, height).into(viewHolder.thumbnail);
                break;
            case 3:
                Picasso.with(context).load(R.drawable.splash_jarvan).centerCrop().resize(width, height).into(viewHolder.thumbnail);
                break;
        }

        return convertView;
    }
}