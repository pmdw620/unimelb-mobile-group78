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
        setContentView(R.layout.activity_welcome);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if(ToolKits.GetBoolean(WelcomeActivity.this,IS_FIRST,false))
                {
                    //如果默认值为false，则没有登陆过，跳转到引导页
                    Log.i("welcome","guide");
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    //将boolean值设置为true
                    ToolKits.putBoolean(WelcomeActivity.this,IS_FIRST,true);
                }else{
                    //否则跳转为主页
                    Log.i("welcome","login");
                    startActivity(new Intent(WelcomeActivity.this,yindaoactivity.class));
                }
                ToolKits.putBoolean(WelcomeActivity.this,IS_FIRST,true);
                return true;
            }
        }).sendEmptyMessageDelayed(0,3000);/*延迟3s*/

    }
}