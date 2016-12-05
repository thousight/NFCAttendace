package com.example.markwen.nfcattendance;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by markw on 12/4/2016.
 */

public class RecyclerViewLayoutAdapter extends RecyclerView.Adapter<RecyclerViewLayoutAdapter.EventViewHolder> {

    private List<String> eventNameList;

    RecyclerViewLayoutAdapter(List<String> eventNameList) {
        this.eventNameList = eventNameList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        EventViewHolder pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.eventName.setText(eventNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventNameList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView eventName;
        EventViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            eventName = (TextView)itemView.findViewById(R.id.eventName);
        }
    }
}
