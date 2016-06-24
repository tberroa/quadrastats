package com.example.tberroa.portal.screens.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.CircleTransform;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

class FriendsAdapter extends ArrayAdapter<Summoner> {

    private final Context context;
    private final List<Summoner> friends;

    public FriendsAdapter(Context context, List<Summoner> friends) {
        super(context, -1, friends);
        this.context = context;
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            // initialize view holder and layout inflater
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_friends, parent, false);

            // initialize views
            viewHolder.profileIcon = (ImageView) convertView.findViewById(R.id.friend_profile_icon_view);
            viewHolder.name = (TextView) convertView.findViewById(R.id.friend_summoner_name_view);

            // set tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set the profile icon
        String url = ScreenUtil.constructProfileIconURL(friends.get(position).profile_icon);
        Picasso.with(context).load(url).fit().transform(new CircleTransform()).into(viewHolder.profileIcon);

        // set name
        viewHolder.name.setText(friends.get(position).name);

        return convertView;
    }

    class ViewHolder {
        TextView name;
        ImageView profileIcon;
    }
}
