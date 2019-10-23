package com.unimelb.ienv;

import android.os.Bundle;

import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.AdapterView;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ImageView;


import java.util.List;
import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import com.bumptech.glide.Glide;

public class ItemAdapter extends BaseAdapter {
    private Context context;
    private List<Item> datas;
    //构造函数需要传入两个必要的参数：上下文对象和数据源
    public ItemAdapter(Context context,List<Item> datas) {
        this.context=context;
        this.datas=datas;
    }
    //返回子项的个数
    @Override
    public int getCount() {
        return datas.size();
    }
    //返回子项对应的对象
    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }
    //返回子项的下标
    @Override
    public long getItemId(int position) {
        return position;
    }
    //返回子项视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item= (Item) getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.item,null);
            viewHolder=new ViewHolder();
            viewHolder.itemImage=(ImageView)view.findViewById(R.id.item_img);
            viewHolder.itemName=(TextView)view.findViewById(R.id.item_name);
            viewHolder.itemScore = (TextView)view.findViewById(R.id.item_score);
            viewHolder.gift = (ImageView)view.findViewById(R.id.gift);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.itemName.setText("   "+item.getItem());
        Glide.with(context)
                .load(item.getImgUrl())
                .into(viewHolder.itemImage);
        viewHolder.itemScore.setText("  Score:"+String.valueOf(item.getScore()));
        viewHolder.gift.setImageResource(R.drawable.exchange);
        Log.d("db",String.valueOf(item.getScore()));
        return view;
    }
    //创建ViewHolder类
    class ViewHolder{
        ImageView itemImage;
        TextView itemName;
        TextView itemScore;
        ImageView gift;

    }
}
