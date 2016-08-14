package com.example.tberroa.portal.screens.stats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.StaticRiotData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

class WinRatesAdapter extends ArrayAdapter<WinRate> {

    public final List<WinRate> winRates;
    public String selectedRole;
    private final Context context;
    private final List<String> names;
    private final StaticRiotData staticRiotData;
    private final Map<String, Map<String, Map<String, WinRate>>> winRatesBySumChamp;

    public WinRatesAdapter(Context context, WinRatePackage winRatePackage) {
        super(context, -1, winRatePackage.winRates);
        this.context = context;
        winRates = winRatePackage.winRates;
        names = winRatePackage.names;
        staticRiotData = winRatePackage.staticRiotData;
        winRatesBySumChamp = winRatePackage.winRatesBySumChamp;
        selectedRole = winRatePackage.selectedRole;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // initialize view holder and layout inflater
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_win_rates, parent, false);

            // initialize views
            viewHolder.name = (TextView) convertView.findViewById(R.id.summoner_name_view);
            viewHolder.played = (TextView) convertView.findViewById(R.id.played_view);
            viewHolder.won = (TextView) convertView.findViewById(R.id.wins_view);
            viewHolder.ratio = (TextView) convertView.findViewById(R.id.win_ratio_view);

            // set tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set text
        viewHolder.name.setText(names.get(position));
        if (winRates.get(position) != null) {
            viewHolder.played.setText(String.valueOf(winRates.get(position).played()));
            viewHolder.won.setText(String.valueOf(winRates.get(position).wins()));
            viewHolder.ratio.setText(winRates.get(position).ratio());
        } else {
            viewHolder.played.setText("0");
            viewHolder.won.setText("0");
            viewHolder.ratio.setText(context.getString(R.string.wrd_not_applicable));
        }

        // cast convert view to linear layout
        LinearLayout layout = (LinearLayout) convertView;

        // clear view
        int children = layout.getChildCount();
        for (int j = 1; j < children; ) {
            layout.removeViewAt(j);
            children = layout.getChildCount();
        }

        // check if layout is supposed to be expanded
        if (winRates.get(position).expanded) {
            // get the win rate objects map
            Map<String, Map<String, WinRate>> intermediate = winRatesBySumChamp.get(names.get(position));
            Map<String, WinRate> winRatesByChamp = intermediate.get(selectedRole);

            // initialize a list to hold the champ views
            List<ChampView> champLayouts = new ArrayList<>();

            // iterate over the win rate map
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int side = (20 * ScreenUtil.screenWidth(context)) / 100;
            Drawable placeholder = ContextCompat.getDrawable(context, R.drawable.ic_placeholder);
            Bitmap bitmap = ((BitmapDrawable) placeholder).getBitmap();
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, side, side, true);
            Drawable resizedPlaceholder = new BitmapDrawable(context.getResources(), resizedBitmap);
            for (Map.Entry<String, WinRate> entry : winRatesByChamp.entrySet()) {
                // initialize views
                @SuppressLint("InflateParams")
                View champLayout = inflater.inflate(R.layout.view_win_rates_champ, null);
                ImageView champIcon = (ImageView) champLayout.findViewById(R.id.champ_icon_view);
                TextView played = (TextView) champLayout.findViewById(R.id.champ_played_view);
                TextView won = (TextView) champLayout.findViewById(R.id.champ_wins_view);
                TextView ratio = (TextView) champLayout.findViewById(R.id.champ_win_ratio_view);

                // set champion icon
                String url = StatsUtil.championIconURL(staticRiotData.version, entry.getKey());
                Picasso.with(context).load(url).resize(side, side)
                        .placeholder(resizedPlaceholder).into(champIcon);

                // set text views
                played.setText(String.valueOf(entry.getValue().played()));
                won.setText(String.valueOf(entry.getValue().wins()));
                ratio.setText(entry.getValue().ratio());

                // add view to list
                ChampView champView = new ChampView();
                champView.played = entry.getValue().played();
                champView.ratio = entry.getValue().ratio();
                champView.champLayout = champLayout;
                champLayouts.add(champView);
            }

            // sort the champ views by most played
            Collections.sort(champLayouts, new Comparator<ChampView>() {
                @Override
                public int compare(ChampView object1, ChampView object2) {
                    int compareVal = object1.played - object2.played;
                    if (compareVal != 0) {
                        return compareVal;
                    } else {
                        return object1.ratio.compareTo(object2.ratio);
                    }
                }
            });

            // insert the champ views into the main layout
            boolean color = true;
            for (int j = champLayouts.size() - 1; j >= 0; j--) {
                View champLayout = champLayouts.get(j).champLayout;
                if (color) {
                    int bgColor = ContextCompat.getColor(context, R.color.white_transparent);
                    champLayout.setBackgroundColor(bgColor);
                }
                layout.addView(champLayout);
                color = !color;
            }
        }
        return layout;
    }

    private class ChampView {
        View champLayout;
        int played;
        String ratio;
    }

    private class ViewHolder {

        TextView name;
        TextView played;
        TextView ratio;
        TextView won;
    }
}
