package com.unimelb.ienv;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class GuideAdapter extends PagerAdapter {

    private List<View> mList;

    public GuideAdapter(List<View> list){
        mList=list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView(mList.get(position));//将指定位置的view去掉
    }

    //重写初始化，将图片添加到指定位置
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ((ViewPager)container).addView(mList.get(position));
        return mList.get(position);
    }
}