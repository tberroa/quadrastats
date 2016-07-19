package com.example.tberroa.portal.screens;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.example.tberroa.portal.R;

public class ScreenUtil {

    private ScreenUtil() {
    }

    public static int championIcon(String name) {
        switch (name) {
            case "aatrox":
                return R.drawable.icon_aatrox;
            case "ahri":
                return R.drawable.icon_ahri;
            case "akali":
                return R.drawable.icon_akali;
            case "alistar":
                return R.drawable.icon_alistar;
            case "amumu":
                return R.drawable.icon_amumu;
            case "anivia":
                return R.drawable.icon_anivia;
            case "annie":
                return R.drawable.icon_annie;
            case "ashe":
                return R.drawable.icon_ashe;
            case "aurelionsol":
                return R.drawable.icon_aurelionsol;
            case "azir":
                return R.drawable.icon_azir;
            case "bard":
                return R.drawable.icon_bard;
            case "blitzcrank":
                return R.drawable.icon_blitzcrank;
            case "brand":
                return R.drawable.icon_brand;
            case "braum":
                return R.drawable.icon_braum;
            case "caitlyn":
                return R.drawable.icon_caitlyn;
            case "cassiopeia":
                return R.drawable.icon_cassiopeia;
            case "chogath":
                return R.drawable.icon_chogath;
            case "corki":
                return R.drawable.icon_corki;
            case "darius":
                return R.drawable.icon_darius;
            case "diana":
                return R.drawable.icon_diana;
            case "draven":
                return R.drawable.icon_draven;
            case "drmundo":
                return R.drawable.icon_drmundo;
            case "ekko":
                return R.drawable.icon_ekko;
            case "elise":
                return R.drawable.icon_elise;
            case "evelynn":
                return R.drawable.icon_evelynn;
            case "ezreal":
                return R.drawable.icon_ezreal;
            case "fiddlesticks":
                return R.drawable.icon_fiddlesticks;
            case "fiora":
                return R.drawable.icon_fiora;
            case "fizz":
                return R.drawable.icon_fizz;
            case "galio":
                return R.drawable.icon_galio;
            case "gangplank":
                return R.drawable.icon_gangplank;
            case "garen":
                return R.drawable.icon_garen;
            case "gnar":
                return R.drawable.icon_gnar;
            case "gragas":
                return R.drawable.icon_gragas;
            case "graves":
                return R.drawable.icon_graves;
            case "hecarim":
                return R.drawable.icon_hecarim;
            case "heimerdinger":
                return R.drawable.icon_heimerdinger;
            case "illaoi":
                return R.drawable.icon_illaoi;
            case "irelia":
                return R.drawable.icon_irelia;
            case "janna":
                return R.drawable.icon_janna;
            case "jarvaniv":
                return R.drawable.icon_jarvaniv;
            case "jax":
                return R.drawable.icon_jax;
            case "jayce":
                return R.drawable.icon_jayce;
            case "jhin":
                return R.drawable.icon_jhin;
            case "jinx":
                return R.drawable.icon_jinx;
            case "kalista":
                return R.drawable.icon_kalista;
            case "karma":
                return R.drawable.icon_karma;
            case "karthus":
                return R.drawable.icon_karthus;
            case "kassadin":
                return R.drawable.icon_kassadin;
            case "katarina":
                return R.drawable.icon_katarina;
            case "kayle":
                return R.drawable.icon_kayle;
            case "kennen":
                return R.drawable.icon_kennen;
            case "khazix":
                return R.drawable.icon_khazix;
            case "kindred":
                return R.drawable.icon_kindred;
            case "kogmaw":
                return R.drawable.icon_kogmaw;
            case "leblanc":
                return R.drawable.icon_leblanc;
            case "leesin":
                return R.drawable.icon_leesin;
            case "leona":
                return R.drawable.icon_leona;
            case "lissandra":
                return R.drawable.icon_lissandra;
            case "lucian":
                return R.drawable.icon_lucian;
            case "lulu":
                return R.drawable.icon_lulu;
            case "lux":
                return R.drawable.icon_lux;
            case "malphite":
                return R.drawable.icon_malphite;
            case "malzahar":
                return R.drawable.icon_malzahar;
            case "maokai":
                return R.drawable.icon_maokai;
            case "masteryi":
                return R.drawable.icon_masteryi;
            case "missfortune":
                return R.drawable.icon_missfortune;
            case "mordekaiser":
                return R.drawable.icon_mordekaiser;
            case "morgana":
                return R.drawable.icon_morgana;
            case "nami":
                return R.drawable.icon_nami;
            case "nasus":
                return R.drawable.icon_nasus;
            case "nauilus":
                return R.drawable.icon_nautilus;
            case "nidalee":
                return R.drawable.icon_nidalee;
            case "nocturne":
                return R.drawable.icon_nocturne;
            case "nunu":
                return R.drawable.icon_nunu;
            case "olaf":
                return R.drawable.icon_olaf;
            case "orianna":
                return R.drawable.icon_orianna;
            case "pantheon":
                return R.drawable.icon_pantheon;
            case "poppy":
                return R.drawable.icon_poppy;
            case "quinn":
                return R.drawable.icon_quinn;
            case "rammus":
                return R.drawable.icon_rammus;
            case "reksai":
                return R.drawable.icon_reksai;
            case "renekton":
                return R.drawable.icon_renekton;
            case "rengar":
                return R.drawable.icon_rengar;
            case "riven":
                return R.drawable.icon_riven;
            case "rumble":
                return R.drawable.icon_rumble;
            case "ryze":
                return R.drawable.icon_ryze;
            case "sejuani":
                return R.drawable.icon_sejuani;
            case "shaco":
                return R.drawable.icon_shaco;
            case "shen":
                return R.drawable.icon_shen;
            case "shyvana":
                return R.drawable.icon_shyvana;
            case "singed":
                return R.drawable.icon_singed;
            case "sion":
                return R.drawable.icon_sion;
            case "sivir":
                return R.drawable.icon_sivir;
            case "skarner":
                return R.drawable.icon_skarner;
            case "sona":
                return R.drawable.icon_sona;
            case "soraka":
                return R.drawable.icon_soraka;
            case "swain":
                return R.drawable.icon_swain;
            case "syndra":
                return R.drawable.icon_syndra;
            case "tahmkench":
                return R.drawable.icon_tahmkench;
            case "taliyah":
                return R.drawable.icon_taliyah;
            case "talon":
                return R.drawable.icon_talon;
            case "taric":
                return R.drawable.icon_taric;
            case "teemo":
                return R.drawable.icon_teemo;
            case "thresh":
                return R.drawable.icon_thresh;
            case "tristana":
                return R.drawable.icon_tristana;
            case "trundle":
                return R.drawable.icon_trundle;
            case "tryndamere":
                return R.drawable.icon_tryndamere;
            case "twistedfate":
                return R.drawable.icon_twistedfate;
            case "twitch":
                return R.drawable.icon_twitch;
            case "udyr":
                return R.drawable.icon_udyr;
            case "urgot":
                return R.drawable.icon_urgot;
            case "varus":
                return R.drawable.icon_varus;
            case "vayne":
                return R.drawable.icon_vayne;
            case "veigar":
                return R.drawable.icon_veigar;
            case "velkoz":
                return R.drawable.icon_velkoz;
            case "vi":
                return R.drawable.icon_vi;
            case "viktor":
                return R.drawable.icon_viktor;
            case "vladimir":
                return R.drawable.icon_vladimir;
            case "volibear":
                return R.drawable.icon_volibear;
            case "warwick":
                return R.drawable.icon_warwick;
            case "wukong":
                return R.drawable.icon_wukong;
            case "xerath":
                return R.drawable.icon_xerath;
            case "xinzhao":
                return R.drawable.icon_xinzhao;
            case "yasuo":
                return R.drawable.icon_yasuo;
            case "yorick":
                return R.drawable.icon_yorick;
            case "zac":
                return R.drawable.icon_zac;
            case "zed":
                return R.drawable.icon_zed;
            case "ziggs":
                return R.drawable.icon_ziggs;
            case "zilean":
                return R.drawable.icon_zilean;
            case "zyra":
                return R.drawable.icon_zyra;
            default:
                return R.drawable.icon_aatrox;
        }
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

    public static String constructItemURL(long itemId) {
        String version = dataDragonVersion();
        if (itemId > 0) {
            return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/item/" + itemId + ".png";
        } else {
            return "http://whosthatchampion.com/static/images/item/NoItem.png";
        }
    }

    public static String constructMasteryURL(long masteryId) {
        String version = dataDragonVersion();
        return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/mastery/" + masteryId + ".png";
    }

    public static String constructProfileIconURL(int iconId) {
        String version = dataDragonVersion();
        return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/profileicon/" + iconId + ".png";
    }

    public static String constructSummonerSpellURL(int spellId) {
        String version = dataDragonVersion();
        switch (spellId) {
            case 1:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerBoost.png";
            case 3:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerExhaust.png";
            case 4:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerFlash.png";
            case 6:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerHaste.png";
            case 7:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerHeal.png";
            case 11:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerSmite.png";
            case 12:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerTeleport.png";
            case 14:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerDot.png";
            case 21:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerBarrier.png";
            default:
                return "http://ddragon.leagueoflegends.com/cdn/" + version + "/img/spell/SummonerFlash.png";
        }
    }

    public static int dpToPx(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
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

    public static int positionIcon(String position) {
        switch (position) {
            case "TOP":
                return R.drawable.ic_pos_top;
            case "JUNGLE":
                return R.drawable.ic_pos_jungle;
            case "MIDDLE":
                return R.drawable.ic_pos_mid;
            case "DUO_CARRY":
                return R.drawable.ic_pos_bot;
            case "DUO_SUPPORT":
                return R.drawable.ic_pos_support;
            default:
                return R.drawable.ic_pos_top;
        }
    }

    public static int screenHeight(Context context) {
        return screenDimensions(context).y;
    }

    public static int screenWidth(Context context) {
        return screenDimensions(context).x;
    }

    private static String dataDragonVersion() {
        return "6.12.1";
    }

    private static Point screenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenDimensions = new Point();
        display.getSize(screenDimensions);
        return screenDimensions;
    }
}
