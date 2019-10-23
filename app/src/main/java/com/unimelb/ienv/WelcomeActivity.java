package com.unimelb.ienv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity {

    public static final String IS_FIRST="is_first";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_welcome);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if(ToolKits.GetBoolean(WelcomeActivity.this,IS_FIRST,false))
                {
                    Log.i("welcome","guide");
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    ToolKits.putBoolean(WelcomeActivity.this,IS_FIRST,true);
                    WelcomeActivity.this.finish();
                }else{
                    Log.i("welcome","login");
                    startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
                    WelcomeActivity.this.finish();
                }
                ToolKits.putBoolean(WelcomeActivity.this,IS_FIRST,true);
                return true;
            }
        }).sendEmptyMessageDelayed(0,1000);

    }
}