package com.xlsol.travo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class newUserActivity extends AppCompatActivity {

    private EditText newEmail,newPassword;
    private Button register;
    private FirebaseAuth mAuth;
    String newEmailval;
    String newPasswordval;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        newEmail = findViewById(R.id.new_user_email);
        newPassword = findViewById(R.id.new_user_password);
        register = findViewById(R.id.registerBtn);
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEmailval= String.valueOf(newEmail.getText());
                newPasswordval= String.valueOf(newPassword.getText());

                createUser();
            }
        });


    }
    private void createUser(){

        mAuth.createUserWithEmailAndPassword(newEmailval, newPasswordval)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                            if(currentUser!=null){
                                Map<String, Object> data1 = new HashMap<>();
                                data1.put("current ticket",null);
                                data1.put("history", FieldValue.arrayUnion());
                                mFirestore.collection("user data").document(currentUser.getUid()).set(data1);
                            }
                            Intent intent = new Intent(newUserActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(newUserActivity.this, ""+task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });



    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }


}