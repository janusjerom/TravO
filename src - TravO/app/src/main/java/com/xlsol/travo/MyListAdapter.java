package com.xlsol.travo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private MyListData[] listdata;
    private Context context;
    private String to,from;

    // RecyclerView recyclerView;
    public MyListAdapter(MyListData[] listdata,Context context ,String to,String from) {
        this.listdata = listdata;
        this.context = context;
        this.to =to;
        this.from =from;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.bus_status_list_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MyListData myListData = listdata[position];
        holder.textView.setText(listdata[position].getUid());
        holder.list_time.setText(listdata[position].getTime());
        holder.list_name.setText(listdata[position].getBusName());
        holder.list_seat.setText(listdata[position].getSeats());
        holder.list_booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"click on item: "+myListData.getUid(),Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(context)
                        .setTitle("Booking Confirmation")
                        .setMessage("Are you sure you want to Book for "+myListData.getBusName())

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, PaymentActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", listdata[position].getUid());
                                intent.putExtra("to",to);
                                intent.putExtra("from",from);
                                context.startActivity(intent);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        /* for implementing click function an the layout
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getUid(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView list_time;
        public TextView list_name;
        public TextView list_seat;
        public TextView list_booknow;


        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.list_id);
            this.list_name=itemView.findViewById(R.id.list_name);
            this.list_time=itemView.findViewById(R.id.list_time);
            this.list_seat=itemView.findViewById(R.id.list_seat);
            this.list_booknow=itemView.findViewById(R.id.list_booknow);

            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}