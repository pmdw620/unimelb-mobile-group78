package com.unimelb.ienv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button regiBtn;
    private Button loginBtn;
    private Button forgotBtn;
    private EditText loginEmail;
    private EditText loginPwd;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // check if user is signed in (non-null)
        currentUser = mAuth.getCurrentUser();

        loginBtn = (Button) findViewById(R.id.loginBtn);
        forgotBtn = (Button) findViewById(R.id.forgotBtn);
        regiBtn = (Button) findViewById(R.id.regiBtn);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPwd = (EditText) findViewById(R.id.loginPwd);
        regiBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgotBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.regiBtn:{
                Intent intent = new Intent(this, registerActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.loginBtn:{
                // perform login authentication function
                mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPwd.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    currentUser = mAuth.getCurrentUser();
                                    updateUI(currentUser);
                                } else{
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                break;
            }
            case R.id.forgotBtn:{
                // perform find lost password function
                Intent intent = new Intent(this, forgotPwdAcitivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(this, dashboardActivity.class);
            startActivity(intent);
        }
    }
}
