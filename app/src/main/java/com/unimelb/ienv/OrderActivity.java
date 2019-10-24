package com.unimelb.ienv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.unimelb.ienv.MainActivity.currentUser;

public class OrderActivity extends AppCompatActivity {

    private ListView listView;
    private List<Order> datas = new ArrayList<Order>();
    private OrderAdapter orderAdapter;
    private FirebaseFirestore db;
    private TextView score;
    private static Context context;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_order);
        context = this;
        ImageView head = (ImageView) findViewById(R.id.order_head);
        head.setImageResource(R.drawable.giftyougot);
        username= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        initDatas();
        listView = (ListView) findViewById(R.id.order_list);
        orderAdapter = new OrderAdapter(this, datas);
        listView.setAdapter(orderAdapter);
    }

    private void initDatas() {
        db = FirebaseFirestore.getInstance();
        db.collection("Order").whereEqualTo("user",username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map doc = document.getData();
                        Order order = new Order(doc.get("item_name").toString(),doc.get("item_url").toString());
                        datas.add(order);
                    }
                    listView = (ListView) findViewById(R.id.order_list);
                    orderAdapter = new OrderAdapter(context, datas);
                    listView.setAdapter(orderAdapter);
                    ((OrderAdapter) orderAdapter).notifyDataSetChanged();
                } else {
                    Log.d("db", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public void onResume() {
        super.onResume();
    }

}
