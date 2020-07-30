package com.xlsol.travo;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.toLowerCase;
import static android.icu.lang.UCharacter.toUpperCase;
import static java.util.Objects.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button check_availability_route, check_availability;
    private ImageButton showSearchCard;
    private CardView hidden_view_home;
    private EditText user_from, user_to;
    private ImageView homeMessage;
    private CalendarView calendarView;
    private LinearLayout linearLayout;
    RecyclerView recyclerView;
    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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
    private String fromtext,totext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View viewObj = inflater.inflate(R.layout.fragment_status, container, false);

        check_availability_route = viewObj.findViewById(R.id.check_availability_route);
        linearLayout = viewObj.findViewById(R.id.pickDate);
        check_availability_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        user_from = viewObj.findViewById(R.id.fromLocation);
        user_to = viewObj.findViewById(R.id.toLocation);
        homeMessage = viewObj.findViewById(R.id.homeMessage);
        calendarView = viewObj.findViewById(R.id.calendarView);

        Calendar cal = Calendar.getInstance();
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        //calendarView.setMaxDate(cal.DAY_OF_MONTH);
        Animation slide_down = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_down_anim);
        check_availability = viewObj.findViewById(R.id.availabilityBtn);
        showSearchCard = viewObj.findViewById(R.id.show_card_btn);
        hidden_view_home = viewObj.findViewById(R.id.hidden_view_home);
        recyclerView = viewObj.findViewById(R.id.Bus_display_view);


        recyclerView.animate().translationY(-2500f);
        check_availability.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View v) {
                View view = viewObj.findViewById(R.id.locator_card);
                hidden_view_home.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                homeMessage.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

                recyclerView.animate().translationY(0f).setDuration(1000);
                //user input from and to
                fromtext = toLowerCase(String.valueOf(user_from.getText()));
                totext = toLowerCase(String.valueOf(user_to.getText()));
                initFirestore(fromtext, totext);
                //MyListAdapter adapter = new MyListAdapter(myListData,getActivity());
                // recyclerView.setHasFixedSize(true);
                // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                //recyclerView.setAdapter(adapter);

                Log.d("s", "onClick: " + fromtext);
                Log.d("s", "onClick: " + totext);

                hidden_view_home.animate()
                        .translationY(0)
                        .alpha(1f)
                        .setDuration(1000);
                view.animate()
                        .translationY(500)
                        .alpha(0.5f)
                        .setDuration(1000);
            }
        });

        showSearchCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                View view = viewObj.findViewById(R.id.locator_card);
                hidden_view_home.setVisibility(View.GONE);
                homeMessage.setVisibility(View.VISIBLE);
                homeMessage.setImageResource(R.drawable.guidelines);

                recyclerView.animate().translationY(-2500f).setDuration(500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.GONE);
                    }
                }, 500);


                view.animate()
                        .translationY(0)
                        .alpha(1f)
                        .setDuration(1000);
                hidden_view_home.animate()
                        .translationY(0)
                        .alpha(0.5f)
                        .setDuration(1000);
            }

        });
        return viewObj;

    }

    private void initFirestore(final String from_user, final String to_user) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        Query query = mFirestore.collection("buses_Database")
                .whereEqualTo("loc." + from_user + ".identifer", true)
                .whereEqualTo("loc." + to_user + ".identifer", true)
                .whereEqualTo("availability", true)
                .whereEqualTo("week_availability.monday", true)
                .limit(3);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", requireNonNull(task.getResult()).size() + "s");
                    int count=0;
                    String uid,time,BusName,seats;
                    MyListData[] myListData = new MyListData[task.getResult().size()];
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //may produce error fallbacks not set
                        String to = requireNonNull(document.get("loc." + to_user + ".pos")).toString();
                        String from = requireNonNull(document.get("loc." + from_user + ".pos")).toString();
                        //int to= document.getLong("loc.a.pos").intValue();
                        if (Integer.parseInt(to) > Integer.parseInt(from)) {
                            uid=document.getId();
                            Log.d("TAG", "Document Id: " + uid);
                            String from_time = requireNonNull(document.get("loc." + from_user + ".time")).toString();
                            String to_time = requireNonNull(document.get("loc." + to_user + ".time")).toString();
                            time=from_time+"--"+to_time;
                            BusName=document.getString("name");
                            seats= String.valueOf(document.getLong("seats"));
                            int timeDiff=Integer.parseInt(to_time)-Integer.parseInt(from_time);
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
                            myListData[count]= new MyListData(uid,time,BusName,seats);
                            count++;
                        }

                    }
                    MyListAdapter adapter = new MyListAdapter(myListData,getActivity(),to_user,from_user);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "onComplete:  Incomplete");
                }
            }
        });

    }
}