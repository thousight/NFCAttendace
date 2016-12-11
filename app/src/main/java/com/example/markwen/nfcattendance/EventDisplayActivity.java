package com.example.markwen.nfcattendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EventDisplayActivity extends AppCompatActivity {

    ArrayList<String> studentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        Intent mIntent = getIntent();
        if (mIntent == null) {
            return;
        }

        String title = mIntent.getStringExtra("title");
        TextView titleText = (TextView) findViewById(R.id.titleTextView);
        titleText.setText(title);
        ListView listViewStudents = (ListView) findViewById(R.id.studentListView);
        studentList = new ArrayList<String>();
        final String strSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File studentFile = new File(strSdPath + "/" + title + ".txt");

        //read
        String text = "";

        try {
            JSONObject event = new JSONObject(studentFile.toString());
            JSONArray students = new JSONArray(event.get("students"));
            String starttime = (String)event.get("starttime");
            String endtime = (String)event.get("endtime");
            TextView startText = (TextView) findViewById((R.id.textStart));
            TextView endText = (TextView) findViewById((R.id.textEnd));
            SimpleDateFormat formatter = new SimpleDateFormat("MM/DD/YYYY hh:mm"); //use formatter to help format millisecond time to readable time
            starttime = formatter.format(startText);
            starttime = formatter.format(endText);
            startText.setText("Start time: " + starttime);
            startText.setText("End time: " + endtime);
            displayStudents(students);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void displayStudents(JSONArray array) {
        ListView listViewStudents = (ListView) findViewById(R.id.studentListView);
        ArrayList<String> studentList = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++){
            try {
                studentList.add(array.getJSONObject(i).getString("name"));
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, studentList);
        listViewStudents.setAdapter(adapter);
    }
}
