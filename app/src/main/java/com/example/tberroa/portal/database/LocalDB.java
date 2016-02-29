package com.example.tberroa.portal.database;

import android.content.Context;

import com.example.tberroa.portal.data.Params;

public class LocalDB {

    public LocalDB(){
    }

    public void Clear(Context context){
        context.deleteDatabase(Params.LOCAL_DB_NAME);
    }
}


