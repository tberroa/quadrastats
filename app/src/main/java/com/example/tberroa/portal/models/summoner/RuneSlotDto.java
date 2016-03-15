package com.example.tberroa.portal.models.summoner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "RuneSlotDto")
public class RuneSlotDto extends Model {

    // parent
    @Column(name = "rune_page")
    RunePageDto runePageDto;

    @Expose
    @Column(name = "rune_id")
    public int runeId;

    @Expose
    @Column(name = "rune_slot_id")
    public int runeSlotId;

    public RuneSlotDto() {
        super();
    }
}
