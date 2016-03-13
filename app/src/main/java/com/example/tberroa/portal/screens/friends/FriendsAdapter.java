package com.example.tberroa.portal.screens.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.DataUtil;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.screens.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

class FriendsAdapter extends ArrayAdapter<SummonerDto>{


    private final Context context;
    private final List<SummonerDto> friends;

    public FriendsAdapter(Context context, List<SummonerDto> friends) {
        super(context, -1, friends);
        this.context = context;
        this.friends = friends;
    }

    class ViewHolder {
        ImageView summonerIcon;
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
            viewHolder.summonerIcon = (ImageView) convertView.findViewById(R.id.summoner_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.summoner_name);

            // set tag
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set the profile icon
        String url = DataUtil.summonerIcon(friends.get(position).profileIconId);
        Picasso.with(context).load(url).fit().transform(new CircleTransform()).into(viewHolder.summonerIcon);

        // set name
        viewHolder.name.setText(friends.get(position).name);

        return convertView;
    }
}
