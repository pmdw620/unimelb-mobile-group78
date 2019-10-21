package com.unimelb.ienv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        view.setMinimumHeight(100);
        ImageView imageView= (ImageView) view.findViewById(R.id.myface);
        TextView textView = (TextView) view.findViewById(R.id.myname);
        TextView pointView = (TextView) view.findViewById(R.id.mypoint);
        imageView.setImageResource(leaderBoard.getImageId());
        textView .setText(leaderBoard.getName());
        pointView.setText(String.valueOf(leaderBoard.getPoints()));
        return view;
    }


}
