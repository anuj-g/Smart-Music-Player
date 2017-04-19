package com.group6.smartplayer.utils;

/**
 * Created by GHANSHYAM on 15-Apr-17.
 */

public class Config
{
    public static final String SC_CLIENT_ID = "8a6abb4b6692b95c9cf9befb3b3ecc14";
    public static final String SC_API_URL = "https://api.soundcloud.com";
    public static final String MC_API_URL = "http://musicovery.com/api/V3";

    public static String fb_name="",fb_dp="",fb_cover="",email="";

    public static void setFBData(String fb_nam2,String fb_dp2,String fb_cover2,String email2) {
        fb_name = fb_nam2;
        fb_dp = fb_dp2;
        fb_cover = fb_cover2;
        email = email2;

    }
}
