package com.unimelb.ienv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivity extends AppCompatActivity {
    private static ItemActivity context;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        context = this;
        final Intent intent = getIntent(); // 取得从上一个Activity当中传递过来的Intent对象
        if (intent != null) {
            final String name = intent.getStringExtra("item");
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(name);
            final int item_score = Integer.parseInt(intent.getStringExtra("item_score"));
            FirebaseFirestore firedb = FirebaseFirestore.getInstance();
            final ImageView largeimg= (ImageView) findViewById(R.id.large_img);
            final TextView describe = (TextView) findViewById(R.id.describe);
            ImageView background= (ImageView) findViewById(R.id.background);
            background.setAlpha(50);
            final ImageButton buy = (ImageButton) findViewById(R.id.buy);
            firedb.collection("Items").document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("test", "DocumentSnapshot data: " + document.getData());
                            Glide.with(context)
                                    .load(document.getData().get("largeimg").toString())
                                    .into(largeimg);
                            describe.setText(document.getData().get("describe").toString());
                        } else {
                            Log.d("test", "No such document");
                        }
                    } else {
                        Log.d("test", "get failed with ", task.getException());
                    }
                }
            });
            buy.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    final String username= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final FirebaseFirestore firedb = FirebaseFirestore.getInstance();
                    firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("test", "DocumentSnapshot data: " + document.getData());
                                    if (Integer.parseInt(document.getData().get("totalPoints").toString()) >= item_score){
                                        firedb.collection("UserCollection").document(username).update("totalPoints",FieldValue.increment(-item_score));
                                        firedb.collection("Items").document(name).update("current_number", FieldValue.increment(-1));
                                        new AlertDialog.Builder(ItemActivity.this)
                                                .setTitle("Congratulations")
                                                .setMessage("It's belong to you!").setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                            }
                                        }).show();
                                    }
                                    else{
                                        new AlertDialog.Builder(ItemActivity.this)
                                                .setTitle("warn")
                                                .setMessage("You don't have enough points to redeem it!").setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                            }
                                        }).show();
                                    }
                                } else {
                                    Log.d("test", "No such document");
                                }
                            } else {
                                Log.d("test", "get failed with ", task.getException());
                            }
                        }
                    });
                }
            });
        }
        else{
            finish();
        }
    }

}
