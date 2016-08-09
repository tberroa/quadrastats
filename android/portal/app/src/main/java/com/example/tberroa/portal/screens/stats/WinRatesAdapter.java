package com.example.tberroa.portal.screens.stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tberroa.portal.R;

import java.util.List;

class WinRatesAdapter extends ArrayAdapter<WinRate> {

    private final Context context;
    private final List<String> names;
    private final List<WinRate> winRates;

    public WinRatesAdapter(Context context, List<WinRate> winRates, List<String> names) {
        super(context, -1, winRates);
        this.context = context;
        this.winRates = winRates;
        this.names = names;
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

        return convertView;
    }

    class ViewHolder {

        TextView name;
        TextView played;
        TextView ratio;
        TextView won;
    }
}
