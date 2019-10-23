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

public class ShopActivity extends AppCompatActivity {

    private ListView listView;
    private List<Item> datas = new ArrayList<Item>();
    private ItemAdapter itemAdapter;
    private FirebaseFirestore db;
    private TextView score;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_shop);
        context = this;
        ImageView soc_background = (ImageView) findViewById(R.id.soc_background);
        ImageView head = (ImageView) findViewById(R.id.head);
        head.setImageResource(R.drawable.shop);
        soc_background.setImageResource(R.drawable.coin);
        final TextView score = (TextView) findViewById(R.id.user_score);
        score.getBackground().setAlpha(0);
        String username= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data: " + document.getData());
                        score.setText(String.valueOf("Your Points: "+ document.getData().get("totalPoints").toString()));
                    } else {
                        Log.d("test", "No such document");
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });
        initDatas();
        listView = (ListView) findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(this, datas);
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ItemActivity.class);
                intent.putExtra("item",datas.get(position).getItem());
                intent.putExtra("item_score",datas.get(position).getScore()+"");
                context.startActivity(intent);
            }
        });

    }
    private void initDatas() {
        db = FirebaseFirestore.getInstance();
        db.collection("Items").whereGreaterThan("current_number",0).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map doc = document.getData();
                        Item item = new Item(doc.get("name").toString(),doc.get("imgUrl").toString(),Integer.parseInt(String.valueOf(doc.get("score"))));
                        datas.add(item);
                    }
                    listView = (ListView) findViewById(R.id.item_list);
                    itemAdapter = new ItemAdapter(context, datas);
                    listView.setAdapter(itemAdapter);
                    ((ItemAdapter) itemAdapter).notifyDataSetChanged();
                } else {
                    Log.d("db", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public void onResume() {
        super.onResume();
        if(datas.size()>1){
            datas.clear();
            initDatas();
        }
        String username= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        final TextView score = (TextView) findViewById(R.id.user_score);
        score.getBackground().setAlpha(0);
        firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data: " + document.getData());
                        score.setText(String.valueOf("Your Points: "+ document.getData().get("totalPoints").toString()));
                    } else {
                        Log.d("test", "No such document");
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });
    }

}
