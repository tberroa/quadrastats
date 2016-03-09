package com.example.tberroa.portal.models.summoner;

// This object contains mastery information.

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "MasteryPageDto")
public class MasteryPageDto extends Model {

    // parent
    @Column(name = "mastery_pages")
    MasteryPagesDto masteryPagesDto;

    @Expose
    @Column(name = "current")
    public boolean current;

    @Expose
    @Column(name = "mastery_page_id")
    public long id;

    @Expose
    @Column(name = "masteries")
    public List<MasteryDto> masteries = new ArrayList<>();

    @Expose
    @Column(name = "name")
    public String name;

    public MasteryPageDto(){
        super();
    }

    public List<MasteryDto> getMasteries(){
        return getMany(MasteryDto.class, "mastery_page");
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (!masteries.isEmpty()){
                for (MasteryDto mastery : masteries){
                    mastery.masteryPageDto = this;
                    mastery.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
