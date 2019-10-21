package com.unimelb.ienv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.widget.ListView;
import java.util.*;

public class TaskFragment extends Fragment {

    private List<TaskItem> list = new ArrayList<>();
    LayoutInflater inflater1;
    ListView lv;
    TaskItemAdapter adapter1;
//    TaskDescAdapter adapter2;
//    int image[] = {R.drawable.leader1,R.drawable.leader2,R.drawable.leader4,R.drawable.leader5,R.drawable.leader6};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater1 = inflater;
        final View rootview = inflater.inflate(R.layout.fragment_task, null);

        lv = (ListView)rootview.findViewById(R.id.taskListView);
        initList1();
        adapter1 = new TaskItemAdapter(getActivity(),R.layout.task_item_layout, list);
        lv.setAdapter(adapter1);

//        lv = (ListView)rootview.findViewById(R.id.descListView);
//        initList2();
//        adapter2 = new TaskDescAdapter(getActivity(),R.layout.des_item_layout, list);
//        lv.setAdapter(adapter2);

        return rootview;
    }

    private void initList1() {
        list.clear();
        for (int i = 0; i < 3; i++) {
            TaskItem item = new TaskItem("Im good","Im bad", R.mipmap.task_image1);
            list.add(item);
        }
    }

//    private void initList2() {
//        list.clear();
//        for (int i = 0; i < 3; i++) {
//            TaskItem item = new TaskItem("Item " + i, R.mipmap.task_image1);
//            list.add(item);
//        }
//    }

}


