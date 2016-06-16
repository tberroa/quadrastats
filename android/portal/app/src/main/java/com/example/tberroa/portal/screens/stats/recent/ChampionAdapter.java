package com.example.tberroa.portal.screens.stats.recent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChampionAdapter extends RecyclerView.Adapter<ChampionAdapter.ChampionViewHolder> {

    private final Context context;
    private final List<String> champions;

    public ChampionAdapter(Context context, List<String> champions) {
        this.context = context;
        this.champions = champions;
    }

    @Override
    public int getItemCount() {
        return champions.size();
    }

    @Override
    public ChampionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context c = viewGroup.getContext();
        View v = LayoutInflater.from(c).inflate(R.layout.element_champion_icon, viewGroup, false);
        return new ChampionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChampionViewHolder clientViewHolder, int i) {
        Picasso.with(context).load(ScreenUtil.getDrawable(champions.get(i))).into(clientViewHolder.image);
    }

    public class ChampionViewHolder extends RecyclerView.ViewHolder {

        final ImageView image;

        ChampionViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
