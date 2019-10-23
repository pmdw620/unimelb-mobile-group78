package com.unimelb.ienv;

import android.content.Context;
import android.content.SharedPreferences;


public class ToolKits{

    public static SharedPreferences getSharedPerferences(Context context){

        return context.getSharedPreferences("包名", Context.MODE_PRIVATE);
    }

    public static void putBoolean(Context context,String key,boolean value){
        SharedPreferences sharedPerferences = getSharedPerferences(context);
        SharedPreferences.Editor editor = sharedPerferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static boolean GetBoolean(Context context,String key,boolean defaultValue){
        return  getSharedPerferences(context).getBoolean(key,defaultValue);
    }
}