package com.example.tberroa.portal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tberroa.portal.R;

public class FriendsAdapter extends ArrayAdapter<String>{


    private final Context context;
    private final String[] summonerNames;

    public FriendsAdapter(Context context, String[] summonerNames) {
        super(context, -1, summonerNames);
        this.context = context;
        this.summonerNames = summonerNames;
    }

    class ViewHolder {
        TextView name;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null){
            // initialize view holder and layout inflater
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_friends, parent, false);

            // initialize views
            viewHolder.name = (TextView) convertView.findViewById(R.id.summoner_name);

            // set tag
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set name
        viewHolder.name.setText(summonerNames[position]);

        return convertView;
    }
}
