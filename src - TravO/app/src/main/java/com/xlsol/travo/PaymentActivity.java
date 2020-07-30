package com.xlsol.travo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.DocumentTransform;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static java.util.Objects.requireNonNull;

public class PaymentActivity extends AppCompatActivity {
    private Button btn;
    private TextView textView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        btn=findViewById(R.id.button);
        textView=findViewById(R.id.textView10);
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        String to=getIntent().getStringExtra("to");
        String from=getIntent().getStringExtra("from");
        textView.setText("Booking for uid"+sessionId+" from:"+from+" to:"+to);
        initFirestore(from,to,sessionId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initFirestore(final String from_user, final String to_user,final String sessionID) {
        final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        DocumentReference query = mFirestore.collection("buses_Database").document(sessionID);
        query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String uid,time,BusName,seats;
                    DocumentSnapshot document =task.getResult();
                        //may produce error fallbacks not set
                        String to = requireNonNull(document.get("loc." + to_user + ".pos")).toString();
                        String from = requireNonNull(document.get("loc." + from_user + ".pos")).toString();
                        //int to= document.getLong("loc.a.pos").intValue();
                            uid=document.getId();
                            Log.d("TAG", "Document Id: " + uid);
                            long time_from= (long) document.get("loc." + from_user + ".time");
                            String from_time = requireNonNull(document.get("loc." + from_user + ".time")).toString();
                            long time_to= (long) document.get("loc." + to_user + ".time");
                            String to_time = requireNonNull(document.get("loc." + to_user + ".time")).toString();
                            BusName=document.getString("name");
                            int timeDiff=Integer.parseInt(to_time)-Integer.parseInt(from_time);
                            String license=document.getString("license");
                            Log.d(TAG, "from time:" + document.get("loc." + from_user + ".time"));
                            Log.d(TAG, "to time:" + document.get("loc." + to_user + ".time"));
                            int diff=((Integer.parseInt(to))-(Integer.parseInt(from)));
                            Log.d(TAG, "Duration:" +timeDiff);
                            Log.d(TAG, "status"+document.getString("status"));
                            int cost;
                            int fixedcharge=7;
                            int variablecharge=10;
                            //terinary operator
                            cost=(diff<=1)?fixedcharge:variablecharge*diff;
                            Log.d(TAG, "Cost:"+cost);

                            //mapping data to the database
                    Map<String, Object> data = new HashMap<>();
                    data.put("bus name",BusName);
                    data.put("license",license);
                    data.put("cost",cost);
                    data.put("distance",diff);
                    data.put("duration",timeDiff);
                    data.put("from",from_user);
                    data.put("from_time",time_from);
                    data.put("to",to_user);
                    data.put("to_time",time_to);
                    data.put("status","Booked");
                    data.put("time", FieldValue.serverTimestamp());
                    final String id = mFirestore.collection("Current tickets").document().getId();
                    mFirestore.collection("Current tickets").document(id).set(data);
                    Log.d(TAG, "onComplete:"+id);


                    mAuth=FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if(currentUser!=null){
                        final Map<String, Object> data1 = new HashMap<>();
                        data1.put("current ticket",id);
                        final DocumentReference userHistoryReference = mFirestore.collection("user data").document(currentUser.getUid());
                        userHistoryReference.update(data1);
                        userHistoryReference.update("history", FieldValue.arrayUnion(id));
                    }
                }
                else {
                    Log.d(TAG, "onComplete:  Incomplete");
                }
            }
        });

    }
}