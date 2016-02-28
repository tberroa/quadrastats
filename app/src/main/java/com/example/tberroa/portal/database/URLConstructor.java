package com.example.tberroa.portal.database;

import com.example.tberroa.portal.data.Params;

public class URLConstructor {

    public URLConstructor(){
    }

    public String summonerIcon(int id){
        return Params.DATA_DRAGON_BASE_URL + "6.4.1/img/profileicon/"+id+".png";
    }

}
