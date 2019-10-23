package com.unimelb.ienv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText email;
    private EditText pwd1;
    private EditText pwd2;
    private EditText username;
    private Button regiSubmitBtn;
    private FirebaseFirestore db;
    private int avatarIndex;
    private de.hdodenhof.circleimageview.CircleImageView avatarSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.email);
        pwd1 = (EditText) findViewById(R.id.pwd1);
        pwd2 = (EditText) findViewById(R.id.pwd2);
        username = (EditText) findViewById(R.id.username);
        regiSubmitBtn = (Button) findViewById(R.id.regiSubmitBtn);
        avatarSelected = findViewById(R.id.avatarSelected);
        avatarIndex = 1;
        // set image view onclick listener (change avatar)
        avatarSelected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                avatarIndex = (int) (Math.random() * 50 + 1);
                int resId = getAvatarResourceId();
                avatarSelected.setImageResource(resId);
            }
        });

        db = FirebaseFirestore.getInstance();

        regiSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if all the fields pass the validation, create a new user in firebase with email and password
                if(validation()){
                    Toast.makeText(getApplicationContext(), "Creating Account in Process.\n It will take only few seconds...", Toast.LENGTH_LONG).show();
                    MainActivity.mAuth.createUserWithEmailAndPassword(email.getText().toString(), pwd1.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                // check if the task (create user process) if complete
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        int resId = getAvatarResourceId();
                                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                        // if the task is completed, store extra fields into firebase;
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("name", username.getText().toString());
                                        user.put("email", email.getText().toString());
                                        user.put("currWeekPoints", 0);
                                        user.put("totalPoints", 0);
                                        user.put("avatarId", resId);
                                        user.put("continueLogin",1);
                                        user.put("lastLoginTime",currentDate);
                                        db.collection("UserCollection").document(MainActivity.mAuth.getCurrentUser().getUid()).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(), "Register Complete", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Error Occurred. Please Contact the admin.", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                        // get uid
                                        MainActivity.mAuth.signInWithEmailAndPassword(email.getText().toString(), pwd1.getText().toString());
                                        MainActivity.currentUser = MainActivity.mAuth.getCurrentUser();
                                        String uid = MainActivity.currentUser.getUid();


                                    }else{
                                        // else, print exception message via Toast.
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                } else{
                    pwd1.setText("");
                    pwd2.setText("");
                }
            }
        });
    }

    private int getAvatarResourceId(){
        String filename = "avatar"+avatarIndex;
        int avatarID = getApplicationContext().getResources().getIdentifier(filename, "drawable", getPackageName());
        return avatarID;
    }

    // validation for user input fields
    private boolean validation(){
        String msg = "";
        String eRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(!email.getText().toString().matches(eRegex)){
            msg = "Invalid format for email.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(pwd1.getText().toString().length()<8 || pwd1.getText().toString().length()>15){
            msg = "The length of password should be ranged 8-15";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(pwd1.getText().toString().compareTo(pwd2.getText().toString())!=0){
            msg = "The passwords are not matched";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
