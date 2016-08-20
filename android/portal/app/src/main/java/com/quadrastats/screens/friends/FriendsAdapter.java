package com.quadrastats.screens.friends;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.models.summoner.Summoner;
import com.quadrastats.screens.CircleTransform;
import com.quadrastats.screens.ScreenUtil;
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
            viewHolder.rankedBorder = (FrameLayout) convertView.findViewById(R.id.ranked_border_layout);
            viewHolder.overlay = (ImageView) convertView.findViewById(R.id.overlay_view);
            viewHolder.profileIcon = (ImageView) convertView.findViewById(R.id.friend_profile_icon_view);
            viewHolder.name = (TextView) convertView.findViewById(R.id.friend_summoner_name_view);
            viewHolder.emblem = (ImageView) convertView.findViewById(R.id.emblem_view);
            viewHolder.tierDivision = (TextView) convertView.findViewById(R.id.tier_division_view);
            viewHolder.lp = (TextView) convertView.findViewById(R.id.lp_view);
            viewHolder.winLossRatio = (TextView) convertView.findViewById(R.id.win_loss_ratio_view);
            viewHolder.series = (LinearLayout) convertView.findViewById(R.id.series_layout);

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

        // resize the emblem image view
        viewHolder.emblem.getLayoutParams().width = side;
        viewHolder.emblem.getLayoutParams().height = side;
        viewHolder.emblem.setLayoutParams(viewHolder.emblem.getLayoutParams());

        // set the emblem
        String tier = friends.get(position).tier;
        String division = friends.get(position).division;
        viewHolder.emblem.setImageResource(getEmblem(tier, division));

        // set the tier/division view
        String tierDivision;
        switch (tier) {
            case Constants.TIER_BRONZE:
                tierDivision = context.getString(R.string.mf_bronze) + " " + division;
                break;
            case Constants.TIER_SILVER:
                tierDivision = context.getString(R.string.mf_silver) + " " + division;
                break;
            case Constants.TIER_GOLD:
                tierDivision = context.getString(R.string.mf_gold) + " " + division;
                break;
            case Constants.TIER_PLAT:
                tierDivision = context.getString(R.string.mf_plat) + " " + division;
                break;
            case Constants.TIER_DIAMOND:
                tierDivision = context.getString(R.string.mf_diamond) + " " + division;
                break;
            case Constants.TIER_MASTER:
                tierDivision = context.getString(R.string.mf_master);
                break;
            case Constants.TIER_CHALLENGER:
                tierDivision = context.getString(R.string.mf_challenger);
                break;
            default:
                tierDivision = "";
                break;
        }
        viewHolder.tierDivision.setText(tierDivision);

        // set the lp view
        int lp = friends.get(position).lp;
        String lpText = lp + "LP";
        viewHolder.lp.setText(lpText);

        // set the win/loss/ratio view
        int wins = friends.get(position).wins;
        int losses = friends.get(position).losses;
        String ratio = ((wins * 100) / (wins + losses)) + "%";
        String winLossRatio = wins + "W " + losses + "L " + ratio;
        viewHolder.winLossRatio.setText(winLossRatio);

        // set the series layout
        String series = friends.get(position).series;
        for (int i = 0; i < viewHolder.series.getChildCount(); i++) {
            ImageView seriesIcon = (ImageView) viewHolder.series.getChildAt(i);
            if (i < series.length()) {
                switch (series.charAt(i)) {
                    case Constants.SERIES_WIN:
                        seriesIcon.setImageResource(R.drawable.ic_series_win);
                        break;
                    case Constants.SERIES_LOSS:
                        seriesIcon.setImageResource(R.drawable.ic_series_loss);
                        break;
                    case Constants.SERIES_UNPLAYED:
                        seriesIcon.setImageResource(R.drawable.ic_series_unplayed);
                        break;
                }
                seriesIcon.setVisibility(View.VISIBLE);
            } else {
                seriesIcon.setVisibility(View.GONE);
            }
        }

        // set the ranked border
        switch (tier) {
            case Constants.TIER_BRONZE:
                viewHolder.rankedBorder.setBackgroundResource(R.drawable.border_bronze);
                break;
            case Constants.TIER_SILVER:
                viewHolder.rankedBorder.setBackgroundResource(R.drawable.border_silver);
                break;
            case Constants.TIER_GOLD:
                viewHolder.rankedBorder.setBackgroundResource(R.drawable.border_gold);
                break;
            case Constants.TIER_PLAT:
                viewHolder.rankedBorder.setBackgroundResource(R.drawable.border_plat);
                break;
            case Constants.TIER_DIAMOND:
                viewHolder.rankedBorder.setBackgroundResource(R.drawable.border_diamond);
                break;
            case Constants.TIER_MASTER:
                viewHolder.rankedBorder.setBackgroundResource(R.drawable.border_master);
                break;
            case Constants.TIER_CHALLENGER:
                viewHolder.rankedBorder.setBackgroundResource(R.drawable.border_challenger);
                break;
        }
        Rect padding = new Rect();
        viewHolder.rankedBorder.getBackground().getPadding(padding);
        viewHolder.overlay.setPadding(padding.left, padding.top, padding.right, padding.bottom);

        return convertView;
    }

    private int getEmblem(String tier, String division) {
        if (Constants.TIER_BRONZE.equals(tier) && Constants.DIVISION_5.equals(division)) {
            return R.drawable.emblem_b5;
        }
        if (Constants.TIER_BRONZE.equals(tier) && Constants.DIVISION_4.equals(division)) {
            return R.drawable.emblem_b4;
        }
        if (Constants.TIER_BRONZE.equals(tier) && Constants.DIVISION_3.equals(division)) {
            return R.drawable.emblem_b3;
        }
        if (Constants.TIER_BRONZE.equals(tier) && Constants.DIVISION_2.equals(division)) {
            return R.drawable.emblem_b2;
        }
        if (Constants.TIER_BRONZE.equals(tier) && Constants.DIVISION_1.equals(division)) {
            return R.drawable.emblem_b1;
        }
        if (Constants.TIER_SILVER.equals(tier) && Constants.DIVISION_5.equals(division)) {
            return R.drawable.emblem_s5;
        }
        if (Constants.TIER_SILVER.equals(tier) && Constants.DIVISION_4.equals(division)) {
            return R.drawable.emblem_s4;
        }
        if (Constants.TIER_SILVER.equals(tier) && Constants.DIVISION_3.equals(division)) {
            return R.drawable.emblem_s3;
        }
        if (Constants.TIER_SILVER.equals(tier) && Constants.DIVISION_2.equals(division)) {
            return R.drawable.emblem_s2;
        }
        if (Constants.TIER_SILVER.equals(tier) && Constants.DIVISION_1.equals(division)) {
            return R.drawable.emblem_s1;
        }
        if (Constants.TIER_GOLD.equals(tier) && Constants.DIVISION_5.equals(division)) {
            return R.drawable.emblem_g5;
        }
        if (Constants.TIER_GOLD.equals(tier) && Constants.DIVISION_4.equals(division)) {
            return R.drawable.emblem_g4;
        }
        if (Constants.TIER_GOLD.equals(tier) && Constants.DIVISION_3.equals(division)) {
            return R.drawable.emblem_g3;
        }
        if (Constants.TIER_GOLD.equals(tier) && Constants.DIVISION_2.equals(division)) {
            return R.drawable.emblem_g2;
        }
        if (Constants.TIER_GOLD.equals(tier) && Constants.DIVISION_1.equals(division)) {
            return R.drawable.emblem_g1;
        }
        if (Constants.TIER_PLAT.equals(tier) && Constants.DIVISION_5.equals(division)) {
            return R.drawable.emblem_p5;
        }
        if (Constants.TIER_PLAT.equals(tier) && Constants.DIVISION_4.equals(division)) {
            return R.drawable.emblem_p4;
        }
        if (Constants.TIER_PLAT.equals(tier) && Constants.DIVISION_3.equals(division)) {
            return R.drawable.emblem_p3;
        }
        if (Constants.TIER_PLAT.equals(tier) && Constants.DIVISION_2.equals(division)) {
            return R.drawable.emblem_p2;
        }
        if (Constants.TIER_PLAT.equals(tier) && Constants.DIVISION_1.equals(division)) {
            return R.drawable.emblem_p1;
        }
        if (Constants.TIER_DIAMOND.equals(tier) && Constants.DIVISION_5.equals(division)) {
            return R.drawable.emblem_d5;
        }
        if (Constants.TIER_DIAMOND.equals(tier) && Constants.DIVISION_4.equals(division)) {
            return R.drawable.emblem_d4;
        }
        if (Constants.TIER_DIAMOND.equals(tier) && Constants.DIVISION_3.equals(division)) {
            return R.drawable.emblem_d3;
        }
        if (Constants.TIER_DIAMOND.equals(tier) && Constants.DIVISION_2.equals(division)) {
            return R.drawable.emblem_d2;
        }
        if (Constants.TIER_DIAMOND.equals(tier) && Constants.DIVISION_1.equals(division)) {
            return R.drawable.emblem_d1;
        }
        if (Constants.TIER_MASTER.equals(tier)) {
            return R.drawable.emblem_m;
        }
        if (Constants.TIER_CHALLENGER.equals(tier)) {
            return R.drawable.emblem_c;
        }

        return R.drawable.emblem_s5;
    }

    private class ViewHolder {

        ImageView emblem;
        TextView lp;
        TextView name;
        ImageView overlay;
        ImageView profileIcon;
        FrameLayout rankedBorder;
        LinearLayout series;
        TextView tierDivision;
        TextView winLossRatio;
    }
}
