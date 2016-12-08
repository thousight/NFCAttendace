package com.example.markwen.nfcattendance;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EventDisplayActivity extends AppCompatActivity {

    ArrayList<String> studentList; //arraylist to handle list of students
    ArrayList<String> messagesReceivedArray = new ArrayList<String>(); //arraylist to handle messages received through NFC
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        getSupportActionBar().setElevation(0);
        setTitle(" ");
        Intent mIntent = getIntent();
        if (mIntent == null) {
            return;
        }

        String title = mIntent.getStringExtra("title");
        TextView titleText = (TextView) findViewById(R.id.titleTextView);
        titleText.setText(title);

        studentList = new ArrayList<String>();
        final String strSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File studentFile = new File(strSdPath + "/NFCAttendance/" + title + ".txt");

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
            CharSequence errorMessage = e.getMessage();
            Snackbar.make(findViewById(R.id.activity_event_display), errorMessage, Snackbar.LENGTH_LONG).show();
        }
    }
    public void displayStudents(JSONArray array) {
        ListView listViewStudents = (ListView) findViewById(R.id.studentListView);
        ArrayList<String> studentList = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++){
            try {
                studentList.add(array.getJSONObject(i).getString("name"));
            } catch (Exception e) {
                CharSequence errorMessage = e.getMessage();
                Snackbar.make(findViewById(R.id.activity_event_display), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, studentList);
        listViewStudents.setAdapter(adapter);
    }

    public void handleNfcIntent(Intent NfcIntent) {
        ListView listViewStudents = (ListView) findViewById(R.id.studentListView);
        ArrayAdapter<String> adapter;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals (NfcIntent.getAction())) {
            Parcelable[] receivedArray = NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                messagesReceivedArray = new ArrayList<String>(); //clear string array
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] records = receivedMessage.getRecords();

                for (NdefRecord record:records) {
                    String tempString = new String(record.getPayload());
                    if (tempString.equals(getPackageName())) { continue; }
                    messagesReceivedArray.add(tempString);
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, messagesReceivedArray);
                    listViewStudents.setAdapter(adapter);
                }
            }
        }
    }
    @Override
    public void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }
}
