package com.unimelb.ienv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by baniel on 1/19/17.
 */

public class TaskDescAdapter extends ArrayAdapter<TaskItem> {
    private int layoutId;

    public TaskDescAdapter(Context context, int layoutId, List<TaskItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskItem item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
//        ImageView imageView = (ImageView) view.findViewById(R.id.task_img);
        TextView textView_up = (TextView) view.findViewById(R.id.task_text_up);
        TextView textView_down = (TextView) view.findViewById(R.id.task_text_down);

//        imageView.setImageResource(item.getImgId());
        textView_up.setText(item.getUpDesc());
        textView_down.setText(item.getDownDesc());

        return view;
    }
}