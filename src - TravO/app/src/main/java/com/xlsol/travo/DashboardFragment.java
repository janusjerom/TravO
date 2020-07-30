package com.xlsol.travo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ConstraintLayout layout;
    private ProgressBar spinner;
    private TextView busname1,status1,license1,from1,to1,duration1,distance1,cost1;
    private Button cancel;
    private FirebaseAuth mAuth;

    public DashboardFragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View viewObj = inflater.inflate(R.layout.fragment_dashboard, container, false);

        layout=viewObj.findViewById(R.id.dashboard_view);
        spinner=viewObj.findViewById(R.id.progressBar1);
        busname1=viewObj.findViewById(R.id.dashboard_busName);
        status1=viewObj.findViewById(R.id.dashboard_status);
        license1=viewObj.findViewById(R.id.dashboard_license);
        from1=viewObj.findViewById(R.id.dashboard_from);
        to1=viewObj.findViewById(R.id.dashboard_to);
        duration1=viewObj.findViewById(R.id.dashboard_duration);
        distance1=viewObj.findViewById(R.id.dashboard_distance);
        cost1=viewObj.findViewById(R.id.dashboard_cost);
        cancel=viewObj.findViewById(R.id.cancelBtn);

        spinner.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        getcurrentTicket();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                //Toast.makeText(view.getContext(),"click on item: "+myListData.getUid(),Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(getContext())
                        .setTitle("Cancel Confirmation")
                        .setMessage("Are you sure you want to Cancel"+busname1.getText())

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                //

            }
        });

        return viewObj;

    }

    private void getcurrentTicket(){
        final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference=mFirestore.collection("user data");
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference query=null;
        if(currentUser!=null){
            query=reference.document(currentUser.getUid());//set to user uid
        }
        //query.get();
        assert query != null;
        query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String currentTicket = Objects.requireNonNull(task.getResult()).getString("current ticket");
                    DocumentReference newquery;
                    if (currentTicket != null) {
                        newquery = mFirestore.collection("Current tickets").
                                document(currentTicket);
                        newquery.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot data = task.getResult();
                                    busname1.setText(Objects.requireNonNull(data).getString("bus name"));
                                    license1.setText(data.getString("license"));
                                    String from= data.getLong("from_time")+data.getString("from");
                                    from1.setText(from);
                                    String to= data.getLong("to_time")+data.getString("to");
                                    to1.setText(to);
                                    duration1.setText(String.valueOf(data.getLong("duration")));
                                    distance1.setText(String.valueOf(data.getLong("distance")));
                                    status1.setText(data.getString("status"));
                                    cost1.setText(String.valueOf(data.getLong("cost")));

                                    spinner.setVisibility(View.GONE);
                                    layout.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                    else {
                        clear();//set text to not found
                    }
                }
            }
        });
    }
    private void clear(){
        busname1.setText(R.string.not_found_text);
        license1.setText(R.string.not_found_text);
        from1.setText(R.string.not_found_text);
        to1.setText(R.string.not_found_text);
        duration1.setText(R.string.not_found_text);
        distance1.setText(R.string.not_found_text);
        status1.setText(R.string.not_found_text);
        cost1.setText(R.string.not_found_text);
    }

}