package com.example.markwen.nfcattendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class EventDisplayActivity extends AppCompatActivity {
    final String strSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static FileObserver observer; //file observer to check for changes to the .txt file
    ArrayList<String> studentList; //arraylist to handle list of students
    ArrayList<String> messagesReceivedArray = new ArrayList<String>(); //arraylist to handle messages received through NFC
    TextView titleText, startTimeText, endTimeText, checkInStatus;
    ListView listViewStudents;
    String title;
    SharedPreferences sharedPref;
    Long start_time;
    Long end_time;
    String students;
    JSONObject event;
    FloatingActionButton backButton;
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
        backButton = (FloatingActionButton) findViewById(R.id.backFAB);

        //read title from title_holder file
        File titleFile = new File(strSdPath + "/NFCAttendance/" + "title_holder.txt");
        BufferedReader br;
        String aDataRow = "";
        String aBuffer = "";
        try {
            br = new BufferedReader(new FileReader(titleFile));

            while ((aDataRow = br.readLine()) != null) {
                title += aDataRow + "\n";
            }
            br.close();

        }
        catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfessorActivity.class));
            }
        });

        // Set title in SharedPreferences
        title = mIntent.getStringExtra("title");
        sharedPref = getSharedPreferences("NFCAttendance", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectedEvent", title);
        editor.apply();

        listViewStudents = (ListView) findViewById(R.id.studentListView);
        studentList = new ArrayList<>();

        //reading file
        File studentFile = new File(strSdPath + "/NFCAttendance/" + title + ".txt");
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
            students = event.get("students").toString();
            Date startDateTime = new Date((Long) event.get("start_time"));
            Date endDateTime = new Date((Long) event.get("end_time"));
            titleText.setText((String) event.get("title"));
            startTimeText.setText("Start time: " + startDateTime.toString());
            endTimeText.setText("End time:   " + endDateTime.toString());
            Date localTime = new Date(System.currentTimeMillis());
            Boolean start = startDateTime.compareTo(localTime) < 0;
            Boolean end = endDateTime.compareTo(localTime) > 0;
            if (!(startDateTime.compareTo(localTime) < 0 && endDateTime.compareTo(localTime) > 0)) {
                checkInStatus.setText("You can only checkin during event hours.");
            }
            displayStudents(students);
        } catch (Exception e) {
            CharSequence errorMessage = e.getMessage();
            Snackbar.make(findViewById(R.id.activity_event_display), errorMessage, Snackbar.LENGTH_LONG).show();
        }

    }
    public void displayStudents(String list) {
        // Trying to fix the problem when there is always an extra value up top
        List<String> array;
        if (list.equals("")){
            array = new ArrayList<>();
        } else {
            array = new ArrayList<>(Arrays.asList(list.split(",")));
        }
        ListView listViewStudents = (ListView) findViewById(R.id.studentListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
        listViewStudents.setAdapter(adapter);
    }

    public void handleNfcIntent(Intent intent) {
        SharedPreferences sharedPref = getSharedPreferences("NFCAttendance", Context.MODE_PRIVATE);
//        String title = sharedPref.getString("selectedEvent", "testing");
        Long localTime = System.currentTimeMillis();

        listViewStudents = (ListView) findViewById(R.id.studentListView);
        ArrayAdapter<String> adapter;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals (intent.getAction())) {
            Parcelable[] receivedArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                messagesReceivedArray = new ArrayList<String>(); //clear string array
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] records = receivedMessage.getRecords();
                String student_name;
                //String device_id;

                int index_check = 0;
                for (NdefRecord record:records) {
                    index_check++;
                    if (index_check % 2 != 0){
                        student_name = new String(record.getPayload());
                        if (student_name.equals(getPackageName())) { continue; }
                        //read file to get students JSONArray to update


                        BufferedReader br;
                        StringBuilder contentString = new StringBuilder();
                        try {

                            br = new BufferedReader(new FileReader(strSdPath + "/NFCAttendance/" + title + ".txt"));
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
                            students = event.get("students").toString();
                            start_time = (Long) event.get("start_time");
                            end_time = (Long) event.get("end_time");
                            title = event.get("title").toString();
                            // Add student into StringArray students
                            if (students.equals("")){
                                students += student_name;
                            } else {
                                students += ("," + student_name);
                            }
                            if (!(start_time < localTime && end_time > localTime)) {
                                finish();
                            }
                        } catch (Exception e) {
                        CharSequence errorMessage = e.getMessage();
                        Snackbar.make(findViewById(R.id.activity_event_display), errorMessage, Snackbar.LENGTH_LONG).show();
                        }
                        //write the updated students array back into the file
                        try {
                            File txtFile = new File(strSdPath + "/NFCAttendance/" + title + ".txt");
                            txtFile.delete();
                            txtFile = new File(strSdPath + "/NFCAttendance/" + title + ".txt"); //make new file
                            FileOutputStream output = new FileOutputStream(txtFile, false);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(output);
                            try {
                                JSONObject obj1 = new JSONObject();
                                obj1.put("students", students);
                                obj1.put("end_time", end_time);
                                obj1.put("start_time", start_time);
                                obj1.put("title", title);

                                String str = obj1.toString();
                                myOutWriter.append(str);
                                myOutWriter.close();
                            } catch (Exception e) {
                                Snackbar.make(findViewById(R.id.activity_event), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Snackbar.make(findViewById(R.id.activity_event), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                        messagesReceivedArray.add(student_name);
                        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messagesReceivedArray);
                        listViewStudents.setAdapter(adapter);
                        Intent restart = new Intent(this, EventDisplayActivity.class);
                        restart.putExtra("title", title);
                        startActivity(restart);
                        finish();
                    }
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        handleNfcIntent(getIntent());
    }

}
