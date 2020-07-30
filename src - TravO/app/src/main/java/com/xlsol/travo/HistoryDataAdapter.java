package com.xlsol.travo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class HistoryDataAdapter extends RecyclerView.Adapter<HistoryDataAdapter.ViewHolder> {
    private currentTicketData[] historyData;
    private Context context;

public HistoryDataAdapter(currentTicketData[] historyData,Context context) {
        this.historyData = historyData;
        this.context = context;

        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.history_list_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final currentTicketData currentTicketData = historyData[position];
        holder.history_busTime.setText(currentTicketData.getBustime());
        holder.history_busName.setText(currentTicketData.getBusname());
        Log.d(TAG, "onClick: Rendered");
        holder.history_busStatus.setText(currentTicketData.getStatus());
        holder.history_busStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,currentTicketData.getBusname(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return historyData.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView history_busName;
        public TextView history_busTime;
        public TextView history_busStatus;

        public RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            history_busName=itemView.findViewById(R.id.history_busName);
            history_busStatus=itemView.findViewById(R.id.history_busStatus);
            history_busTime=itemView.findViewById(R.id.history_busTime);
            relativeLayout=itemView.findViewById(R.id.relativeLayout);
        }
    }
}
