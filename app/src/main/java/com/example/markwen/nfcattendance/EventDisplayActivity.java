package com.example.markwen.nfcattendance;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

        String title = mIntent.getStringExtra("string1");
        TextView titleText = (TextView) findViewById((R.id.textView));
        titleText.setText(title);
        ListView listViewStudents = (ListView) findViewById(R.id.);
        studentList = new ArrayList<String>();
        final String strSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File studentFile = new File(strSdPath + "/" + title + ".txt");

        //read
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader myReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(strSdPath + "/NFCAttendance/" + title + ".txt"))));
            String aDataRow = "";

            while ((aDataRow = myReader.readLine()) != null) {

                text.append(aDataRow);
                text.append('\n');
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
