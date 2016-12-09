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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class EventDisplayActivity extends AppCompatActivity {

    ArrayList<String> studentList; //arraylist to handle list of students
    ArrayList<String> messagesReceivedArray = new ArrayList<String>(); //arraylist to handle messages received through NFC
    TextView titleText, startTimeText, endTimeText, checkInStatus;
    ListView listViewStudents;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        getSupportActionBar().setElevation(0);
        setTitle("");
        Intent mIntent = getIntent();
        if (mIntent == null) {
            return;
        }

        titleText = (TextView) findViewById(R.id.titleTextView);
        startTimeText = (TextView) findViewById(R.id.startTimeTextView);
        endTimeText = (TextView) findViewById(R.id.endTimeTextView);
        checkInStatus = (TextView) findViewById(R.id.checkInStatusTextView);

        // Set title
        title = mIntent.getStringExtra("title");

        listViewStudents = (ListView) findViewById(R.id.studentListView);
        studentList = new ArrayList<String>();
        final String strSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        //reading file
        JSONObject event;
        JSONArray students;
        File studentFile = new File(strSdPath + "/NFCAttendance/" + title + ".txt");
        BufferedReader br;
        StringBuilder contentString = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(studentFile));
            String line;
            while ((line = br.readLine()) != null) {
                contentString.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        try {
            event = new JSONObject(contentString.toString());
            students = event.getJSONArray("students");
            Date startDateTime = new Date((Long) event.get("start_time"));
            Date endDateTime = new Date((Long) event.get("end_time"));
//            DateFormat formatter = new SimpleDateFormat("MM/DD/yyyy hh:mm"); //use formatter to help format millisecond time to readable time
//            String starttime = formatter.parse(startDateTime.toString()).toString();
//            String endtime = formatter.parse(endDateTime.toString()).toString();
            titleText.setText((String) event.get("title"));
            startTimeText.setText("Start time: " + startDateTime.toString());
            endTimeText.setText("End time:   " + endDateTime.toString());
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

    public void handleNfcIntent(Intent intent) {
        listViewStudents = (ListView) findViewById(R.id.studentListView);
        ArrayAdapter<String> adapter;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals (intent.getAction())) {
            Parcelable[] receivedArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

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
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handleNfcIntent(getIntent());
    }
}
