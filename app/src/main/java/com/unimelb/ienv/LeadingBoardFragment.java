package com.unimelb.ienv;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeadingBoardFragment extends Fragment {
    private List<LeaderBoard> list = new ArrayList<>();
    LayoutInflater inflater1;
    ListView lv;
    ListAdapter adapter;
    int image[] = {R.drawable.leader1,R.drawable.leader2,R.drawable.leader4,R.drawable.leader5,R.drawable.leader6};
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater1 = inflater;
        final View rootview =   inflater.inflate(R.layout.fragment_leadingboard, null);
        lv = (ListView)rootview.findViewById(R.id.leaderListView);

        initList();
        adapter = new LeaderBoardAdapter(getActivity(),R.layout.leadedboard_item,list);
        lv.setAdapter(adapter);
        String username= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data: " + document.getData());
                        View myname = (TextView)rootview.findViewById(R.id.myuser);
                        View mypoint = (TextView)rootview.findViewById(R.id.myuserpoint);
                        ((TextView) myname).setText((String)document.getData().get("name"));
                        System.out.println(document.getData().get("currWeekPoints").toString());
                        ((TextView) mypoint).setText(document.getData().get("currWeekPoints").toString());
                    } else {
                        Log.d("test", "No such document");
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });

        return rootview;

    }
    private void initList() {
        FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        firedb.collection("UserCollection").orderBy("currWeekPoints", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int num = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("test", document.getId() + " => " + document.getData());
                                Log.d("test", document.getId() + " => " + (String)document.getData().get("name")+(Long)document.getData().get("currWeekPoints"));
                                LeaderBoard item = new LeaderBoard(image[num],(String)document.getData().get("name"),((Long) document.getData().get("currWeekPoints")).intValue());
                                list.add(item);
                                num+=1;
                            }

                            View rootview =   inflater1.inflate(R.layout.fragment_leadingboard, null);
                            System.out.println(list.toArray().toString());
                            adapter = new LeaderBoardAdapter(getActivity(),R.layout.leadedboard_item,list);
                            lv.setAdapter(adapter);
                            ((LeaderBoardAdapter) adapter).notifyDataSetChanged();

                        } else {
                            Log.d("test", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
//    public void initList(){
//
//    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }
}
