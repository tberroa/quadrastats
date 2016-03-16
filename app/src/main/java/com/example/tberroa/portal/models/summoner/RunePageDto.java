package com.example.tberroa.portal.models.summoner;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "RunePageDto")
public class RunePageDto extends Model {

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
    public Set<RuneSlotDto> slots = new HashSet<>();
    // parent
    @Column(name = "rune_pages")
    RunePagesDto runePagesDto;

    public RunePageDto() {
        super();
    }

    public List<RuneSlotDto> getSlots() {
        return getMany(RuneSlotDto.class, "rune_page");
    }

    public void cascadeSave() {
        ActiveAndroid.beginTransaction();
        try {
            save();
            if (!slots.isEmpty()) {
                for (RuneSlotDto slot : slots) {
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
