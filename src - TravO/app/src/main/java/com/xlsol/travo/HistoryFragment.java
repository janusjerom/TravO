package com.xlsol.travo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static java.util.Objects.requireNonNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ConstraintLayout layout;
    private ProgressBar spinner;

    private RecyclerView recyclerView;
    private Button refresh,clear;
    private FirebaseAuth mAuth;
    final String[] busname = new String[1];
    final String[] bustime = new String[1];
    final String[] status = new String[1];

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        final View viewObj = inflater.inflate(R.layout.fragment_history, container, false);
        layout=viewObj.findViewById(R.id.history_view);
        recyclerView=viewObj.findViewById(R.id.history_recyler_view);
        spinner = viewObj.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        updateHistory();
        return viewObj;
    }


    private void updateHistory(){
        final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        CollectionReference reference=mFirestore.collection("user data");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference query=null;

        if(currentUser!=null){
            query=reference.document(currentUser.getUid());//set to user uid
        }
        requireNonNull(query).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> array = null;
                if (task.isSuccessful()) {
                    array = (ArrayList<String>) task.getResult().get("history");

                }
                if (array != null) {
                    final currentTicketData[] data = new currentTicketData[array.size()];
                    Log.d(TAG, "onComplete: " + array.size());
                    final int[] count = {0};
                    int count_1=0;
                    for (int i = 0; i < array.size(); i++) {
                        final String val = array.get(i);
                        DocumentReference newquery = mFirestore.collection("Current tickets").document(val);
                        if(i==0){
                          for(String va:array){
                            data[count_1] = new currentTicketData(busname[0], bustime[0], status[0]);
                            count_1++;
                          }
                        }

                        final ArrayList<String> finalArray = array;
                        newquery.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.d(TAG, "onComplete: asasd" + val);
                                if (task.isSuccessful()) {
                                    DocumentSnapshot var = task.getResult();
                                    Log.d(TAG, "onComplete: setp 2");
                                    Log.d(TAG, "onComplete: val" + task.getResult());
                                    busname[0] = requireNonNull(var).getString("bus name");
                                    String from_time = String.valueOf(var.getLong("from_time"));
                                    String to_time = String.valueOf(var.getLong("to_time"));
                                    bustime[0] = (from_time + "--" + to_time);
                                    status[0] = var.getString("status");
                                    Log.d(TAG, "onComplete: inside values " + busname[0] + bustime[0] + status[0]);
                                    data[count[0]] = new currentTicketData(busname[0], bustime[0], status[0]);
                                    count[0]++;
                                    if(data.length == finalArray.size()){
                                        Log.d(TAG, "onComplete: array valuse " + busname[0] + bustime[0] + status[0]);
                                        HistoryDataAdapter adapter = new HistoryDataAdapter(data, getActivity());
                                        adapter.getItemCount();
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        recyclerView.setAdapter(adapter);
                                        spinner.setVisibility(View.GONE);
                                        layout.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.d(TAG, "onComplete: asasd" + val);
                                }
                            }

                        });
                    }

                }

            }

        });

    }

}