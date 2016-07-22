package com.example.tberroa.portal.screens.stats;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.models.datadragon.Champion;

import java.util.Map;

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
        colors[4] = R.color.purple;
        colors[5] = R.color.red;
        colors[6] = R.color.sky_blue;
        colors[7] = R.color.yellow;
        return colors;
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
                return R.color.purple;
            case 5:
                return R.color.red;
            case 6:
                return R.color.sky_blue;
            case 7:
                return R.color.yellow;
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

    public static int positionIcon(String position) {
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
}



