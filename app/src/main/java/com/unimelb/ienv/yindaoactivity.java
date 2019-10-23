package com.unimelb.ienv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class yindaoactivity extends AppCompatActivity {

    private ViewPager pager;
    private Button btnstart;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yindao);
        pager=(ViewPager)findViewById(R.id.view_pager);
        btnstart=(Button) findViewById(R.id.start_btn);
        //为button添加监听事件,点击后切换到主界面
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(yindaoactivity.this,MainActivity.class));
            }
        });
        //初始化ViewPage中的3张图片
        initViewPate();
    }

    private void initViewPate(){
        //List存放3张图片
        List<View> list=new ArrayList<View>();
        ImageView img1=new ImageView(this);
        //设置这几张图片的源
        img1.setImageResource(R.drawable.guide_1);
        list.add(img1);
        ImageView img2=new ImageView(this);
        img2.setImageResource(R.drawable.guide_2);
        list.add(img2);
        ImageView img3=new ImageView(this);
        img3.setImageResource(R.drawable.guide_3);
        list.add(img3);

        GuideAdapter myAdapter=new GuideAdapter(list);
        pager.setAdapter(myAdapter);

        //前两张没有"立即体验"的按钮，等到第三张才有
        //设置pager的监听器
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //如果是第三张图片，显示btn
                if(position==2)btnstart.setVisibility(View.VISIBLE);
                else btnstart.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
}