package com.example.tberroa.portal.screens.stats;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.screens.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class LegendAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final GridView legend;
    private final List<String> names;
    private final Boolean notFiveMan;
    private final int viewWidth;
    private final List<Integer> widths;

    public LegendAdapter(Context context, List<String> names, Boolean notFiveMan, GridView legend, int viewWidth) {
        super(context, -1, names);
        this.context = context;
        this.names = names;
        this.notFiveMan = notFiveMan;
        this.legend = legend;
        this.viewWidth = viewWidth;
        widths = new ArrayList<>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        TextView nameView;

        if (convertView == null) {
            // initialize view holder and layout inflater
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.element_textview, parent, false);

            // initialize view
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.text_view);
            nameView = viewHolder.nameView;

            // set tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            nameView = viewHolder.nameView;
        }

        // set name
        nameView.setText(names.get(position));

        // set color
        if ((notFiveMan != null) && (notFiveMan) && (position == (names.size() - 1))) {
            nameView.setTextColor(ContextCompat.getColor(context, R.color.gray));
        } else {
            nameView.setTextColor(ContextCompat.getColor(context, StatsUtil.intToColor(position)));
        }

        // set legend column width
        nameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                nameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                widths.add(nameView.getWidth());
                if (position == (names.size() - 1)) {
                    new SetLegendColumnWidth().execute();
                }
            }
        });

        return convertView;
    }

    private class SetLegendColumnWidth extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // pre padding number of column
            int rawNumOfColumns = viewWidth / Collections.max(widths);

            // padding needed
            int padding = ScreenUtil.dpToPx(context, 3) * rawNumOfColumns;

            // format legend
            int paddedNumOfColumns = (viewWidth - padding) / Collections.max(widths);
            if (names.size() < paddedNumOfColumns) {
                legend.setNumColumns(names.size());
            } else {
                legend.setNumColumns(paddedNumOfColumns);
            }
            legend.setColumnWidth(Collections.max(widths));
            legend.setVisibility(View.VISIBLE);
        }
    }

    private class ViewHolder {

        TextView nameView;
    }
}
