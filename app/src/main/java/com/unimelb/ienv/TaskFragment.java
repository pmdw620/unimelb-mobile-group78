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
//    int image[] = {R.drawable.dining, R.drawable.recycle, R.drawable.quiz};
//    String[] desc_up = {"Eco IN Dining", "Eco IN Recycle", "Eco IN Quiz"};
//    String[] desc_down = {"Scan the QR code and get the eco dining score",
//            "Scan the QR code and get the score", "Answer 10 Eco quiz questsions and get 50 score"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater1 = inflater;
        final View rootview = inflater.inflate(R.layout.fragment_task, null);
        return rootview;
    }

}


