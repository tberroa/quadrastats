package com.example.tberroa.portal.screens.stats;

import java.util.ArrayList;
import java.util.List;

public class StatsUtil {

    private StatsUtil() {
    }

    public static int championKey(String name) {
        switch (name) {
            case "aatrox":
                return 266;
            case "ahri":
                return 103;
            case "akali":
                return 84;
            case "alistar":
                return 12;
            case "amumu":
                return 32;
            case "anivia":
                return 34;
            case "annie":
                return 1;
            case "ashe":
                return 22;
            case "aurelionsol":
                return 136;
            case "azir":
                return 268;
            case "bard":
                return 432;
            case "blitzcrank":
                return 53;
            case "brand":
                return 63;
            case "braum":
                return 201;
            case "caitlyn":
                return 51;
            case "cassiopeia":
                return 69;
            case "chogath":
                return 31;
            case "corki":
                return 42;
            case "darius":
                return 122;
            case "diana":
                return 131;
            case "drmundo":
                return 36;
            case "draven":
                return 119;
            case "ekko":
                return 245;
            case "elise":
                return 60;
            case "evelynn":
                return 28;
            case "ezreal":
                return 81;
            case "fiddlesticks":
                return 9;
            case "fiora":
                return 114;
            case "fizz":
                return 105;
            case "galio":
                return 3;
            case "gangplank":
                return 41;
            case "garen":
                return 86;
            case "gnar":
                return 150;
            case "gragas":
                return 79;
            case "graves":
                return 104;
            case "hecarim":
                return 120;
            case "heimerdinger":
                return 74;
            case "illaoi":
                return 420;
            case "irelia":
                return 39;
            case "janna":
                return 40;
            case "jarvaniv":
                return 59;
            case "jax":
                return 24;
            case "jayce":
                return 126;
            case "jhin":
                return 202;
            case "jinx":
                return 222;
            case "kalista":
                return 429;
            case "karma":
                return 43;
            case "karthus":
                return 30;
            case "kassadin":
                return 38;
            case "katarina":
                return 55;
            case "kayle":
                return 10;
            case "kennen":
                return 85;
            case "khazix":
                return 121;
            case "kindred":
                return 203;
            case "kogmaw":
                return 96;
            case "leblanc":
                return 7;
            case "leesin":
                return 64;
            case "leona":
                return 89;
            case "lissandra":
                return 127;
            case "lucian":
                return 236;
            case "lulu":
                return 117;
            case "lux":
                return 99;
            case "malphite":
                return 54;
            case "malzahar":
                return 90;
            case "maokai":
                return 57;
            case "masteryi":
                return 11;
            case "missfortune":
                return 21;
            case "mordekaiser":
                return 82;
            case "morgana":
                return 25;
            case "nami":
                return 267;
            case "nasus":
                return 75;
            case "nauilus":
                return 111;
            case "nidalee":
                return 76;
            case "nocturne":
                return 56;
            case "nunu":
                return 20;
            case "olaf":
                return 2;
            case "orianna":
                return 61;
            case "pantheon":
                return 80;
            case "poppy":
                return 78;
            case "quinn":
                return 133;
            case "rammus":
                return 33;
            case "reksai":
                return 421;
            case "renekton":
                return 58;
            case "rengar":
                return 107;
            case "riven":
                return 92;
            case "rumble":
                return 68;
            case "ryze":
                return 13;
            case "sejuani":
                return 113;
            case "shaco":
                return 35;
            case "shen":
                return 98;
            case "shyvana":
                return 102;
            case "singed":
                return 27;
            case "sion":
                return 14;
            case "sivir":
                return 15;
            case "skarner":
                return 72;
            case "sona":
                return 37;
            case "soraka":
                return 16;
            case "swain":
                return 50;
            case "syndra":
                return 134;
            case "tahmkench":
                return 223;
            case "taliyah":
                return 163;
            case "talon":
                return 91;
            case "taric":
                return 44;
            case "teemo":
                return 17;
            case "thresh":
                return 412;
            case "tristana":
                return 18;
            case "trundle":
                return 48;
            case "tryndamere":
                return 23;
            case "twistedfate":
                return 4;
            case "twitch":
                return 29;
            case "udyr":
                return 77;
            case "urgot":
                return 6;
            case "varus":
                return 110;
            case "vayne":
                return 67;
            case "veigar":
                return 45;
            case "velkoz":
                return 161;
            case "vi":
                return 254;
            case "viktor":
                return 112;
            case "vladimir":
                return 8;
            case "volibear":
                return 106;
            case "warwick":
                return 19;
            case "wukong":
                return 62;
            case "xerath":
                return 101;
            case "xinzhao":
                return 5;
            case "yasuo":
                return 157;
            case "yorick":
                return 83;
            case "zac":
                return 154;
            case "zed":
                return 238;
            case "ziggs":
                return 115;
            case "zilean":
                return 26;
            case "zyra":
                return 143;
            default:
                return 266; // aatrox
        }
    }

    public static String championName(long key) {
        switch ((int) key) {
            case 266:
                return "aatrox";
            case 103:
                return "ahri";
            case 84:
                return "akali";
            case 12:
                return "alistar";
            case 32:
                return "amumu";
            case 34:
                return "anivia";
            case 1:
                return "annie";
            case 22:
                return "ashe";
            case 136:
                return "aurelionsol";
            case 268:
                return "azir";
            case 432:
                return "bard";
            case 53:
                return "blitzcrank";
            case 63:
                return "brand";
            case 201:
                return "braum";
            case 51:
                return "caitlyn";
            case 69:
                return "cassiopeia";
            case 31:
                return "chogath";
            case 42:
                return "corki";
            case 122:
                return "darius";
            case 131:
                return "diana";
            case 36:
                return "drmundo";
            case 119:
                return "draven";
            case 245:
                return "ekko";
            case 60:
                return "elise";
            case 28:
                return "evelynn";
            case 81:
                return "ezreal";
            case 9:
                return "fiddlesticks";
            case 114:
                return "fiora";
            case 105:
                return "fizz";
            case 3:
                return "galio";
            case 41:
                return "gangplank";
            case 86:
                return "garen";
            case 150:
                return "gnar";
            case 79:
                return "gragas";
            case 104:
                return "graves";
            case 120:
                return "hecarim";
            case 74:
                return "heimerdinger";
            case 420:
                return "illaoi";
            case 39:
                return "irelia";
            case 40:
                return "janna";
            case 59:
                return "jarvaniv";
            case 24:
                return "jax";
            case 126:
                return "jayce";
            case 202:
                return "jhin";
            case 222:
                return "jinx";
            case 429:
                return "kalista";
            case 43:
                return "karma";
            case 30:
                return "karthus";
            case 38:
                return "kassadin";
            case 55:
                return "katarina";
            case 10:
                return "kayle";
            case 85:
                return "kennen";
            case 121:
                return "khazix";
            case 203:
                return "kindred";
            case 96:
                return "kogmaw";
            case 7:
                return "leblanc";
            case 64:
                return "leesin";
            case 89:
                return "leona";
            case 127:
                return "lissandra";
            case 236:
                return "lucian";
            case 117:
                return "lulu";
            case 99:
                return "lux";
            case 54:
                return "malphite";
            case 90:
                return "malzahar";
            case 57:
                return "maokai";
            case 11:
                return "masteryi";
            case 21:
                return "missfortune";
            case 82:
                return "mordekaiser";
            case 25:
                return "morgana";
            case 267:
                return "nami";
            case 75:
                return "nasus";
            case 111:
                return "nauilus";
            case 76:
                return "nidalee";
            case 56:
                return "nocturne";
            case 20:
                return "nunu";
            case 2:
                return "olaf";
            case 61:
                return "orianna";
            case 80:
                return "pantheon";
            case 78:
                return "poppy";
            case 133:
                return "quinn";
            case 33:
                return "rammus";
            case 421:
                return "reksai";
            case 58:
                return "renekton";
            case 107:
                return "rengar";
            case 92:
                return "riven";
            case 68:
                return "rumble";
            case 13:
                return "ryze";
            case 113:
                return "sejuani";
            case 35:
                return "shaco";
            case 98:
                return "shen";
            case 102:
                return "shyvana";
            case 27:
                return "singed";
            case 14:
                return "sion";
            case 15:
                return "sivir";
            case 72:
                return "skarner";
            case 37:
                return "sona";
            case 16:
                return "soraka";
            case 50:
                return "swain";
            case 134:
                return "syndra";
            case 223:
                return "tahmkench";
            case 163:
                return "taliyah";
            case 91:
                return "talon";
            case 44:
                return "taric";
            case 17:
                return "teemo";
            case 412:
                return "thresh";
            case 18:
                return "tristana";
            case 48:
                return "trundle";
            case 23:
                return "tryndamere";
            case 4:
                return "twistedfate";
            case 29:
                return "twitch";
            case 77:
                return "udyr";
            case 6:
                return "urgot";
            case 110:
                return "varus";
            case 67:
                return "vayne";
            case 45:
                return "veigar";
            case 161:
                return "velkoz";
            case 254:
                return "vi";
            case 112:
                return "viktor";
            case 8:
                return "vladimir";
            case 106:
                return "volibear";
            case 19:
                return "warwick";
            case 62:
                return "wukong";
            case 101:
                return "xerath";
            case 5:
                return "xinzhao";
            case 157:
                return "yasuo";
            case 83:
                return "yorick";
            case 154:
                return "zac";
            case 238:
                return "zed";
            case 115:
                return "ziggs";
            case 26:
                return "zilean";
            case 143:
                return "zyra";
            default:
                return "aatrox";
        }
    }

    public static List<String> championNames() {
        List<String> champions = new ArrayList<>(150);
        champions.add("aatrox");
        champions.add("ahri");
        champions.add("akali");
        champions.add("alistar");
        champions.add("amumu");
        champions.add("anivia");
        champions.add("annie");
        champions.add("ashe");
        champions.add("aurelionsol");
        champions.add("azir");
        champions.add("bard");
        champions.add("blitzcrank");
        champions.add("brand");
        champions.add("braum");
        champions.add("caitlyn");
        champions.add("cassiopeia");
        champions.add("chogath");
        champions.add("corki");
        champions.add("darius");
        champions.add("diana");
        champions.add("draven");
        champions.add("drmundo");
        champions.add("ekko");
        champions.add("elise");
        champions.add("evelynn");
        champions.add("ezreal");
        champions.add("fiddlesticks");
        champions.add("fiora");
        champions.add("fizz");
        champions.add("galio");
        champions.add("gangplank");
        champions.add("garen");
        champions.add("gnar");
        champions.add("gragas");
        champions.add("graves");
        champions.add("hecarim");
        champions.add("heimerdinger");
        champions.add("illaoi");
        champions.add("irelia");
        champions.add("janna");
        champions.add("jarvaniv");
        champions.add("jax");
        champions.add("jayce");
        champions.add("jhin");
        champions.add("jinx");
        champions.add("kalista");
        champions.add("karma");
        champions.add("karthus");
        champions.add("kassadin");
        champions.add("katarina");
        champions.add("kayle");
        champions.add("kennen");
        champions.add("khazix");
        champions.add("kindred");
        champions.add("kogmaw");
        champions.add("leblanc");
        champions.add("leesin");
        champions.add("leona");
        champions.add("lissandra");
        champions.add("lucian");
        champions.add("lulu");
        champions.add("lux");
        champions.add("malphite");
        champions.add("malzahar");
        champions.add("maokai");
        champions.add("masteryi");
        champions.add("missfortune");
        champions.add("mordekaiser");
        champions.add("morgana");
        champions.add("nami");
        champions.add("nasus");
        champions.add("nauilus");
        champions.add("nidalee");
        champions.add("nocturne");
        champions.add("nunu");
        champions.add("olaf");
        champions.add("orianna");
        champions.add("pantheon");
        champions.add("poppy");
        champions.add("quinn");
        champions.add("rammus");
        champions.add("reksai");
        champions.add("renekton");
        champions.add("rengar");
        champions.add("riven");
        champions.add("rumble");
        champions.add("ryze");
        champions.add("sejuani");
        champions.add("shaco");
        champions.add("shen");
        champions.add("shyvana");
        champions.add("singed");
        champions.add("sion");
        champions.add("sivir");
        champions.add("skarner");
        champions.add("sona");
        champions.add("soraka");
        champions.add("swain");
        champions.add("syndra");
        champions.add("tahmkench");
        champions.add("taliyah");
        champions.add("talon");
        champions.add("taric");
        champions.add("teemo");
        champions.add("thresh");
        champions.add("tristana");
        champions.add("trundle");
        champions.add("tryndamere");
        champions.add("twistedfate");
        champions.add("twitch");
        champions.add("udyr");
        champions.add("urgot");
        champions.add("varus");
        champions.add("vayne");
        champions.add("veigar");
        champions.add("velkoz");
        champions.add("vi");
        champions.add("viktor");
        champions.add("vladimir");
        champions.add("volibear");
        champions.add("warwick");
        champions.add("wukong");
        champions.add("xerath");
        champions.add("xinzhao");
        champions.add("yasuo");
        champions.add("yorick");
        champions.add("zac");
        champions.add("zed");
        champions.add("ziggs");
        champions.add("zilean");
        champions.add("zyra");

        return champions;
    }
}



