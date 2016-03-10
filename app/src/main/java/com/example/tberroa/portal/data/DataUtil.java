package com.example.tberroa.portal.data;

public class DataUtil {

    private DataUtil(){
    }

    static public String summonerIcon(int iconId){
        return Params.DATA_DRAGON_BASE_URL + "6.4.1/img/profileicon/"+iconId+".png";
    }

}
