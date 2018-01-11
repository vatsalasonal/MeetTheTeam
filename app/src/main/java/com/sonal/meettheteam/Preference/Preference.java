package com.sonal.meettheteam.Preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sonal.meettheteam.Model.PeopleModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Preference {
    private static final String FILE_NAME = "patient.pref";
    private static Preference mInstance = null;

    public static Preference getInstance() {
        if (null == mInstance) {
            mInstance = new Preference();
        }
        return mInstance;
    }

    public void putString(Context context, String key, String value){
        SharedPreferences mPref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();

        Gson gSon = new Gson();
        String json = gSon.toJson(value);
        editor.putString(key,json);
        editor.commit();
    }

    public String getString(Context context, String key ){

        SharedPreferences mPref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPref.getString(key, "");
        String res;

        if (json.isEmpty()){
            res = "";
        }else {

            Type type =  new TypeToken<String>(){}.getType();
            res = gson.fromJson(json, type);
        }
        return res;
    }

    public void putPeopleList(Context context, String key, ArrayList<PeopleModel> value){
        SharedPreferences mPref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();

        Gson gSon = new Gson();
        String json = gSon.toJson(value);
        editor.putString(key,json);
        editor.commit();
    }

    public ArrayList<PeopleModel> getPeopleList(Context context, String key ){

        ArrayList<PeopleModel> peoples = new ArrayList<PeopleModel>();
        SharedPreferences mPref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPref.getString(key, "");

        if (json.isEmpty()){
            peoples = new ArrayList<>();
        }else {
            Type type =  new TypeToken<ArrayList<PeopleModel>>(){}.getType();
            peoples = gson.fromJson(json, type);
        }
        return peoples;
    }

}
