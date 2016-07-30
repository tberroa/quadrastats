package com.example.tberroa.portal.screens.stats;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.models.datadragon.Champion;
import com.example.tberroa.portal.screens.RoundTransform;
import com.example.tberroa.portal.screens.StaticRiotData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class StatsUtil {

    private StatsUtil() {
    }

    public static String championIconURL(String version, String key) {
        return Constants.URL_DATA_DRAGON + version + Constants.URL_CHAMPION + key + Constants.URL_IMAGE_TYPE;
    }

    public static String championKey(long id, Map<String, Champion> championsMap) {
        for (Map.Entry<String, Champion> entry : championsMap.entrySet()) {
            if (id == entry.getValue().id) {
                return entry.getValue().key;
            }
        }
        return "";
    }

    public static int[] chartColors() {
        int[] colors = new int[8];
        colors[0] = R.color.blue;
        colors[1] = R.color.green;
        colors[2] = R.color.orange;
        colors[3] = R.color.pink;
        colors[4] = R.color.yellow;
        colors[5] = R.color.purple;
        colors[6] = R.color.sky_blue;
        colors[7] = R.color.red;
        return colors;
    }

    public static void createLegend(CreateLegendPackage createLegendPackage) {
        long championId = createLegendPackage.championId;
        Context context = createLegendPackage.context;
        int iconSide = createLegendPackage.iconSide;
        Set<String> names = createLegendPackage.names;
        Boolean notFiveMan = createLegendPackage.notFiveMan;
        String position = createLegendPackage.position;
        StaticRiotData staticRiotData = createLegendPackage.staticRiotData;
        View view = createLegendPackage.view;
        Integer viewWidth = createLegendPackage.viewWidth;

        // set position icon
        ImageView positionIcon = (ImageView) view.findViewById(R.id.position_view);
        if (position != null) {
            positionIcon.getLayoutParams().width = iconSide;
            positionIcon.getLayoutParams().height = iconSide;
            positionIcon.setLayoutParams(positionIcon.getLayoutParams());
            positionIcon.setImageResource(positionIcon(position));
            positionIcon.setVisibility(View.VISIBLE);
        } else {
            positionIcon.setVisibility(View.GONE);
        }

        // set champion icon
        ImageView championIcon = (ImageView) view.findViewById(R.id.champ_icon_view);
        if (championId > 0) {
            championIcon.getLayoutParams().width = iconSide;
            championIcon.getLayoutParams().height = iconSide;
            championIcon.setLayoutParams(positionIcon.getLayoutParams());
            String key = championKey(championId, staticRiotData.championsMap);
            String url = championIconURL(staticRiotData.version, key);
            Picasso.with(context).load(url).resize(iconSide, iconSide)
                    .placeholder(R.drawable.ic_placeholder).transform(new RoundTransform()).into(championIcon);
            championIcon.setVisibility(View.VISIBLE);
        } else {
            championIcon.setVisibility(View.GONE);
        }

        // set names
        GridView legend = (GridView) view.findViewById(R.id.names_grid);
        LegendAdapter adapter = new LegendAdapter(context, new ArrayList<>(names), notFiveMan, legend, viewWidth);
        legend.setVisibility(View.INVISIBLE);
        legend.setNumColumns(1);
        legend.setColumnWidth(1000);
        legend.setAdapter(adapter);
    }

    public static int intToColor(int i) {
        switch (i % 8) {
            case 0:
                return R.color.blue;
            case 1:
                return R.color.green;
            case 2:
                return R.color.orange;
            case 3:
                return R.color.pink;
            case 4:
                return R.color.yellow;
            case 5:
                return R.color.purple;
            case 6:
                return R.color.sky_blue;
            case 7:
                return R.color.red;
            default:
                return R.color.blue;
        }
    }

    public static String itemURL(String version, long itemId) {
        if (itemId > 0) {
            return Constants.URL_DATA_DRAGON + version + Constants.URL_ITEM + itemId + Constants.URL_IMAGE_TYPE;
        } else {
            return Constants.UI_NO_ITEM;
        }
    }

    public static String masteryURL(String version, long masteryId) {
        return Constants.URL_DATA_DRAGON + version + Constants.URL_MASTERY + masteryId + Constants.URL_IMAGE_TYPE;
    }

    public static String summonerSpellURL(String version, int spellId) {
        switch (spellId) {
            case 1:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_CLEANSE;
            case 3:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_EXHAUST;
            case 4:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_FLASH;
            case 6:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_GHOST;
            case 7:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_HEAL;
            case 11:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_SMITE;
            case 12:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_TELEPORT;
            case 14:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_IGNITE;
            case 21:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_BARRIER;
            default:
                return Constants.URL_DATA_DRAGON + version + Constants.URL_SS_FLASH;
        }
    }

    private static int positionIcon(String position) {
        switch (position) {
            case Constants.POS_TOP:
                return R.drawable.ic_pos_top;
            case Constants.POS_JUNGLE:
                return R.drawable.ic_pos_jungle;
            case Constants.POS_MID:
                return R.drawable.ic_pos_mid;
            case Constants.POS_BOT:
                return R.drawable.ic_pos_bot;
            case Constants.POS_SUPPORT:
                return R.drawable.ic_pos_support;
            default:
                return R.drawable.ic_pos_top;
        }
    }
}



