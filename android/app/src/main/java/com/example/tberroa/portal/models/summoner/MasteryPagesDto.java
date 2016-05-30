package com.example.tberroa.portal.models.summoner;

// This object contains masteries information.

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection", "CanBeFinal"})
@Table(name = "MasteryPagesDto")
public class MasteryPagesDto extends Model {

    @Expose
    @Column(name = "pages")
    public Set<MasteryPageDto> pages = new HashSet<>();

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    public MasteryPagesDto() {
        super();
    }

    public List<MasteryPageDto> getPages() {
        return getMany(MasteryPageDto.class, "mastery_pages");
    }

    public void cascadeSave() {
        ActiveAndroid.beginTransaction();
        try {
            save();
            if (!pages.isEmpty()) {
                for (MasteryPageDto page : pages) {
                    page.masteryPagesDto = this;
                    page.cascadeSave();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
