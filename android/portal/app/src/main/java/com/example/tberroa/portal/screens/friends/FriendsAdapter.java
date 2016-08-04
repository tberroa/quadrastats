package com.example.tberroa.portal.screens.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.CircleTransform;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

class FriendsAdapter extends ArrayAdapter<Summoner> {

    private final Context context;
    private final List<Summoner> friends;
    private final String version;

    public FriendsAdapter(Context context, List<Summoner> friends, String version) {
        super(context, -1, friends);
        this.context = context;
        this.friends = friends;
        this.version = version;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // initialize view holder and layout inflater
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_friends, parent, false);

            // initialize views
            viewHolder.rankedBorder = (ImageView) convertView.findViewById(R.id.ranked_border_view);
            viewHolder.profileIcon = (ImageView) convertView.findViewById(R.id.friend_profile_icon_view);
            viewHolder.name = (TextView) convertView.findViewById(R.id.friend_summoner_name_view);

            // set tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // get dimensions
        int side = ScreenUtil.screenHeight(context) / 7;

        // resize the profile icon image view
        viewHolder.profileIcon.getLayoutParams().width = side;
        viewHolder.profileIcon.getLayoutParams().height = side;
        viewHolder.profileIcon.setLayoutParams(viewHolder.profileIcon.getLayoutParams());

        // set the profile icon
        String url = ScreenUtil.profileIconURL(version, friends.get(position).profile_icon);
        Picasso.with(context).load(url).resize(side, side)
                .placeholder(R.drawable.ic_placeholder).transform(new CircleTransform()).into(viewHolder.profileIcon);

        // set name
        viewHolder.name.setText(friends.get(position).name);

        // set the ranked border
        String tier = friends.get(position).tier;
        if (tier == null) {
            viewHolder.rankedBorder.setImageResource(R.drawable.border_silver);
        } else {
            switch (tier) {
                case Constants.RANK_BRONZE:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_silver);
                    break;
                case Constants.RANK_SILVER:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_silver);
                    break;
                case Constants.RANK_GOLD:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_gold);
                    break;
                case Constants.RANK_PLAT:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_plat);
                    break;
                case Constants.RANK_DIAMOND:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_plat);
                    break;
                case Constants.RANK_MASTER:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_plat);
                    break;
                case Constants.RANK_CHALLENGER:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_plat);
                    break;
                default:
                    viewHolder.rankedBorder.setImageResource(R.drawable.border_silver);
                    break;
            }
        }

        return convertView;
    }

    class ViewHolder {

        TextView name;
        ImageView profileIcon;
        ImageView rankedBorder;
    }
}
