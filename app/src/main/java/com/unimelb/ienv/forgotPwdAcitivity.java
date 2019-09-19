package com.unimelb.ienv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPwdAcitivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText forgotPageEmail;
    private Button emailVeriBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpwd);
        mAuth = FirebaseAuth.getInstance();

        forgotPageEmail = (EditText) findViewById(R.id.forgotEmail);
        emailVeriBtn = (Button) findViewById(R.id.emailVeriBtn);

        emailVeriBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate(forgotPageEmail.getText().toString())){
                    mAuth.sendPasswordResetEmail(forgotPageEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    finish();
                                }
                            });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid Format of Email", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validate(String email){
        String eRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(!email.matches(eRegex)){
            return false;
        }else{
            return true;
        }
    }
}
