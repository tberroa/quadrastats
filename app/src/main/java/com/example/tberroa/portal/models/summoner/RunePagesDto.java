package com.example.tberroa.portal.models.summoner;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "RunePagesDto")
public class RunePagesDto extends Model {

    @Expose
    @Column(name = "pages")
    public Set<RunePageDto> pages;

    public List<RunePageDto> getPages(){
        return getMany(RunePageDto.class, "rune_pages");
    }

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    public RunePagesDto(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (pages != null){
                for (RunePageDto page : pages){
                    page.runePagesDto = this;
                    page.cascadeSave();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
