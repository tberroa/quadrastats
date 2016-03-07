package com.example.tberroa.portal.models.summoner;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "RunePageDto")
public class RunePageDto extends Model {

    // parent
    @Expose
    @Column(name = "rune_pages")
    RunePagesDto runePagesDto;

    @Expose
    @Column(name = "current")
    public boolean current;

    @Expose
    @Column(name = "rune_page_id")
    public long id;

    @Expose
    @Column(name = "name")
    public String name;

    @Expose
    @Column(name = "slots")
    public Set<RuneSlotDto> slots;

    public List<RuneSlotDto> getSlots(){
        return getMany(RuneSlotDto.class, "rune_page");
    }

    public RunePageDto(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (slots != null){
                for (RuneSlotDto slot : slots){
                    slot.runePageDto = this;
                    slot.save();
                }
            }
            ActiveAndroid.endTransaction();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
