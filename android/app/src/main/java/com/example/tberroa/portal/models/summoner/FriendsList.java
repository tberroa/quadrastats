package com.example.tberroa.portal.models.summoner;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "FriendsList")
public class FriendsList extends Model {

    @Column(name = "friends")
    public List<SummonerDto> friends;

    public FriendsList() {
        super();
    }

    public List<SummonerDto> getFriends() {
        return getMany(SummonerDto.class, "friend");
    }

    public void cascadeSave() {
        ActiveAndroid.beginTransaction();
        try {
            save();
            if (!friends.isEmpty()) {
                for (SummonerDto friend : friends) {
                    friend.friend = this;
                    friend.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

}
