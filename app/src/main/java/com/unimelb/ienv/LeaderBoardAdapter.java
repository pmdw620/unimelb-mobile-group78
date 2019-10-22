package com.unimelb.ienv;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LeaderBoardAdapter extends ArrayAdapter<LeaderBoard> {
    private int resource;

    public LeaderBoardAdapter(Context context, int resource, List<LeaderBoard> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        LeaderBoard leaderBoard = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resource, parent,false
        );
        int height = getHeight(getContext());
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) view.getLayoutParams();
        lp.height = height/11;
        view.setLayoutParams(lp);
        ImageView imageView= (ImageView) view.findViewById(R.id.myface);
        ViewGroup.LayoutParams  ip= (ViewGroup.LayoutParams) imageView.getLayoutParams();
        ip.height = height/11-60;
        ip.width = height/11-60;
        imageView.setLayoutParams(ip);
        TextView textView = (TextView) view.findViewById(R.id.myname);
        TextView pointView = (TextView) view.findViewById(R.id.mypoint);
        imageView.setImageResource(leaderBoard.getImageId());
        textView .setText(leaderBoard.getName());
        pointView.setText(String.valueOf(leaderBoard.getPoints()));
        return view;
    }
    public static int getHeight(Context mContext){
        int height=0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.HONEYCOMB){
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        }else{
            height = display.getHeight();  // deprecated
        }
        return height;
    }

}
