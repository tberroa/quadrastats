package com.example.tberroa.portal.screens;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.example.tberroa.portal.R;

public class ScreenUtil {

    private ScreenUtil() {
    }

    static public String constructIconURL(int iconId) {
        return "http://ddragon.leagueoflegends.com/cdn/6.12.1/img/profileicon/" + iconId + ".png";
    }

    static public int dpToPx(Context context, int dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    static public int getChampionIcon(String name){
        switch (name){
            case "aatrox":
                return R.drawable.aatrox;
            case "ahri":
                return R.drawable.ahri;
            case "akali":
                return R.drawable.akali;
            case "alistar":
                return R.drawable.alistar;
            case "amumu":
                return R.drawable.amumu;
            case "anivia":
                return R.drawable.anivia;
            case "annie":
                return R.drawable.annie;
            case "ashe":
                return R.drawable.ashe;
            case "aurelionsol":
                return R.drawable.aurelionsol;
            case "azir":
                return R.drawable.azir;
            case "bard":
                return R.drawable.bard;
            case "blitzcrank":
                return R.drawable.blitzcrank;
            case "brand":
                return R.drawable.brand;
            case "braum":
                return R.drawable.braum;
            case "caitlyn":
                return R.drawable.caitlyn;
            case "cassiopeia":
                return R.drawable.cassiopeia;
            case "chogath":
                return R.drawable.chogath;
            case "corki":
                return R.drawable.corki;
            case "darius":
                return R.drawable.darius;
            case "diana":
                return R.drawable.diana;
            case "draven":
                return R.drawable.draven;
            case "drmundo":
                return R.drawable.drmundo;
            case "ekko":
                return R.drawable.ekko;
            case "elise":
                return R.drawable.elise;
            case "evelynn":
                return R.drawable.evelynn;
            case "ezreal":
                return R.drawable.ezreal;
            case "fiddlesticks":
                return R.drawable.fiddlesticks;
            case "fiora":
                return R.drawable.fiora;
            case "fizz":
                return R.drawable.fizz;
            case "galio":
                return R.drawable.galio;
            case "gangplank":
                return R.drawable.gangplank;
            case "garen":
                return R.drawable.garen;
            case "gnar":
                return R.drawable.gnar;
            case "gragas":
                return R.drawable.gragas;
            case "graves":
                return R.drawable.graves;
            case "hecarim":
                return R.drawable.hecarim;
            case "heimerdinger":
                return R.drawable.heimerdinger;
            case "illaoi":
                return R.drawable.illaoi;
            case "irelia":
                return R.drawable.irelia;
            case "janna":
                return R.drawable.janna;
            case "jarvaniv":
                return R.drawable.jarvaniv;
            case "jax":
                return R.drawable.jax;
            case "jayce":
                return R.drawable.jayce;
            case "jhin":
                return R.drawable.jhin;
            case "jinx":
                return R.drawable.jinx;
            case "kalista":
                return R.drawable.kalista;
            case "karma":
                return R.drawable.karma;
            case "karthus":
                return R.drawable.karthus;
            case "kassadin":
                return R.drawable.kassadin;
            case "katarina":
                return R.drawable.katarina;
            case "kayle":
                return R.drawable.kayle;
            case "kennen":
                return R.drawable.kennen;
            case "khazix":
                return R.drawable.khazix;
            case "kindred":
                return R.drawable.kindred;
            case "kogmaw":
                return R.drawable.kogmaw;
            case "leblanc":
                return R.drawable.leblanc;
            case "leesin":
                return R.drawable.leesin;
            case "leona":
                return R.drawable.leona;
            case "lissandra":
                return R.drawable.lissandra;
            case "lucian":
                return R.drawable.lucian;
            case "lulu":
                return R.drawable.lulu;
            case "lux":
                return R.drawable.lux;
            case "malphite":
                return R.drawable.malphite;
            case "malzahar":
                return R.drawable.malzahar;
            case "maokai":
                return R.drawable.maokai;
            case "masteryi":
                return R.drawable.masteryi;
            case "missfortune":
                return R.drawable.missfortune;
            case "mordekaiser":
                return R.drawable.mordekaiser;
            case "morgana":
                return R.drawable.morgana;
            case "nami":
                return R.drawable.nami;
            case "nasus":
                return R.drawable.nasus;
            case "nauilus":
                return R.drawable.nautilus;
            case "nidalee":
                return R.drawable.nidalee;
            case "nocturne":
                return R.drawable.nocturne;
            case "nunu":
                return R.drawable.nunu;
            case "olaf":
                return R.drawable.olaf;
            case "orianna":
                return R.drawable.orianna;
            case "pantheon":
                return R.drawable.pantheon;
            case "poppy":
                return R.drawable.poppy;
            case "quinn":
                return R.drawable.quinn;
            case "rammus":
                return R.drawable.rammus;
            case "reksai":
                return R.drawable.reksai;
            case "renekton":
                return R.drawable.renekton;
            case "rengar":
                return R.drawable.rengar;
            case "riven":
                return R.drawable.riven;
            case "rumble":
                return R.drawable.rumble;
            case "ryze":
                return R.drawable.ryze;
            case "sejuani":
                return R.drawable.sejuani;
            case "shaco":
                return R.drawable.shaco;
            case "shen":
                return R.drawable.shen;
            case "shyvana":
                return R.drawable.shyvana;
            case "singed":
                return R.drawable.singed;
            case "sion":
                return R.drawable.sion;
            case "sivir":
                return R.drawable.sivir;
            case "skarner":
                return R.drawable.skarner;
            case "sona":
                return R.drawable.sona;
            case "soraka":
                return R.drawable.soraka;
            case "swain":
                return R.drawable.swain;
            case "syndra":
                return R.drawable.syndra;
            case "tahmkench":
                return R.drawable.tahmkench;
            case "taliyah":
                return R.drawable.taliyah;
            case "talon":
                return R.drawable.talon;
            case "taric":
                return R.drawable.taric;
            case "teemo":
                return R.drawable.teemo;
            case "thresh":
                return R.drawable.thresh;
            case "tristana":
                return R.drawable.tristana;
            case "trundle":
                return R.drawable.trundle;
            case "tryndamere":
                return R.drawable.tryndamere;
            case "twistedfate":
                return R.drawable.twistedfate;
            case "twitch":
                return R.drawable.twitch;
            case "udyr":
                return R.drawable.udyr;
            case "urgot":
                return R.drawable.urgot;
            case "varus":
                return R.drawable.varus;
            case "vayne":
                return R.drawable.vayne;
            case "veigar":
                return R.drawable.veigar;
            case "velkoz":
                return R.drawable.velkoz;
            case "vi":
                return R.drawable.vi;
            case "viktor":
                return R.drawable.viktor;
            case "vladimir":
                return R.drawable.vladimir;
            case "volibear":
                return R.drawable.volibear;
            case "warwick":
                return R.drawable.warwick;
            case "wukong":
                return R.drawable.wukong;
            case "xerath":
                return R.drawable.xerath;
            case "xinzhao":
                return R.drawable.xinzhao;
            case "yasuo":
                return R.drawable.yasuo;
            case "yorick":
                return R.drawable.yorick;
            case "zac":
                return R.drawable.zac;
            case "zed":
                return R.drawable.zed;
            case "ziggs":
                return R.drawable.ziggs;
            case "zilean":
                return R.drawable.zilean;
            case "zyra":
                return R.drawable.zyra;
            default:
                return R.drawable.aatrox;
        }
    }

    static public int[] getChartColors(){
        int[] colors = new int[8];
        colors[0] = R.color.series_blue;
        colors[1] = R.color.series_green;
        colors[2] = R.color.series_orange;
        colors[3] = R.color.series_pink;
        colors[4] = R.color.series_purple;
        colors[5] = R.color.series_red;
        colors[6] = R.color.series_sky;
        colors[7] = R.color.series_yellow;
        return colors;
    }

    static public int getPositionIcon(String position){
        switch (position){
            case "TOP":
                return R.drawable.pos_top;
            case "JUNGLE":
                return R.drawable.pos_jungle;
            case "MIDDLE":
                return R.drawable.pos_mid;
            case "DUO_CARRY":
                return R.drawable.pos_bot;
            case "DUO_SUPPORT":
                return R.drawable.pos_support;
            default:
                return R.drawable.pos_top;
        }
    }

    static public int getScreenHeight(Context context) {
        return getScreenDimensions(context).y;
    }

    static public int getScreenWidth(Context context) {
        return getScreenDimensions(context).x;
    }

    static public int intToColor(int i){
        switch (i % 8) {
            case 0:
                return R.color.series_blue;
            case 1:
                return R.color.series_green;
            case 2:
                return R.color.series_orange;
            case 3:
                return R.color.series_pink;
            case 4:
                return R.color.series_purple;
            case 5:
                return R.color.series_red;
            case 6:
                return R.color.series_sky;
            case 7:
                return R.color.series_yellow;
            default:
                return R.color.series_blue;
        }
    }

    static private Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenDimensions = new Point();
        display.getSize(screenDimensions);
        return screenDimensions;
    }
}
