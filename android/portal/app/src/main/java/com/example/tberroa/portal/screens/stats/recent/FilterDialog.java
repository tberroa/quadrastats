package com.example.tberroa.portal.screens.stats.recent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.tberroa.portal.R;

import java.util.ArrayList;
import java.util.List;

public class FilterDialog extends Dialog {

    final Context context;

    public FilterDialog(Context context){
        super(context, R.style.DialogStyle);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.element_champion_list);

        // initialize list of champions
        List<String> champions = new ArrayList<>(131);
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
        
        // initialize recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.champion_list_recycler_view);
        recyclerView.setAdapter(new ChampionAdapter(context, champions));
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));

        setTitle(R.string.filter_by_champion);
        setCancelable(true);
    }

}
