package com.example.markwen.nfcattendance;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by markw on 12/4/2016.
 */

public class RecyclerViewLayoutAdapter extends RecyclerView.Adapter<RecyclerViewLayoutAdapter.EventViewHolder> {
    final String strSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private List<String> eventNameList;

    RecyclerViewLayoutAdapter(List<String> eventNameList) {
        this.eventNameList = eventNameList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.eventName.setText(eventNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventNameList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView eventName;
        EventViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            eventName = (TextView)itemView.findViewById(R.id.eventName);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start new activity
                    Intent displayEventIntent = new Intent(itemView.getContext(), EventDisplayActivity.class);
                    itemView.getContext().startActivity(displayEventIntent);

                    try {
                        File txtFile = new File(strSdPath + "/NFCAttendance/" + "title_holder.txt"); //make new file
                        FileOutputStream output = new FileOutputStream(txtFile, false);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(output);
                        String str = eventName.getText().toString(); //take title name and write to txt file
                        myOutWriter.append(str);
                        myOutWriter.close();
                    } catch (Exception e) {
                        Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
            // Long press card to delete the file
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String filename = eventName.getText().toString();
                    File file = new File(strSdPath + "/NFCAttendance/" + filename + ".txt"); // get file
                    file.delete(); // delete file
                    // update view
                    eventNameList.remove(filename);
                    Snackbar.make(itemView, filename + " is deleted", Snackbar.LENGTH_LONG).show();
                    notifyDataSetChanged();
                    return true;
                }
            });
        }
    }
}
