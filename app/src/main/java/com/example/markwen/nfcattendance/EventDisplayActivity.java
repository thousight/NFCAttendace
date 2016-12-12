package com.example.markwen.nfcattendance;

import android.content.Intent;
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
import java.io.InputStream;
import java.io.InputStreamReader;
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
    Long start_time;
    Long end_time;
    String students, devices;
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

        // Set title
        title = mIntent.getStringExtra("title");

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
            if (!(startDateTime.compareTo(localTime) < 0 && endDateTime.compareTo(localTime) > 0)) {
                checkInStatus.setText("You cannot check in now.");
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
        Long localTime = System.currentTimeMillis();

        listViewStudents = (ListView) findViewById(R.id.studentListView);
        ArrayAdapter<String> adapter;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals (intent.getAction())) {
            Parcelable[] receivedArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            // Only perform actions when there are items in the array
            if(receivedArray != null) {
                //Make sure you close all streams.
                BufferedReader br;
                StringBuilder contentString = new StringBuilder();
                try {
                    br = new BufferedReader(new FileReader(strSdPath + "/NFCAttendance/title_hoder.txt"));
                    String line = br.readLine();
                    while ((line = br.readLine()) != null) {
                        contentString.append(line);
                    }
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String title = contentString.toString();

                messagesReceivedArray = new ArrayList<>(); //clear string array
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] records = receivedMessage.getRecords();
                String student_name = "";
                String device_id = "";
                int index_check = 0;
                for (NdefRecord record:records) {
                    index_check++;
                    if (index_check % 2 == 0){ // Setting deviceID
                        device_id = new String(record.getPayload());
                    }
                    if (index_check % 2 == 1) { // Setting student name
                        student_name = new String(record.getPayload());
                    }
                    if (student_name.equals(getPackageName())) { continue; }
                }

                //read file to get students JSONArray to update
                try {
                    br = new BufferedReader(new FileReader(strSdPath + "/NFCAttendance/" + title + ".txt"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        contentString.append(line);
                    }
                    br.close();

                    // Read JSON file
                    event = new JSONObject(contentString.toString());
                    students = event.get("students").toString();
                    devices = event.get("devices").toString();
                    start_time = (Long) event.get("start_time");
                    end_time = (Long) event.get("end_time");
                    title = event.get("title").toString();

                    if (!(start_time < localTime && end_time > localTime)) {
                        // stop if local time is not in event time range
                        Toast.makeText(this, "Not time to check in yet", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (devices.contains(device_id)) {
                        // if devices already existed in the list
                        Toast.makeText(this, "This device is already used to check in", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Add student into StringArray students
                        if (students.equals("")){
                            students += student_name;
                        } else {
                            students += ("," + student_name);
                        }
                        // Add device into StringArray students
                        if (devices.equals("")){
                            devices += device_id;
                        } else {
                            devices += ("," + device_id);
                        }
                    }

                    //write the updated students array back into the file
                    File txtFile = new File(strSdPath + "/NFCAttendance/" + title + ".txt");
                    txtFile.delete();
                    txtFile = new File(strSdPath + "/NFCAttendance/" + title + ".txt"); //make new file
                    FileOutputStream output = new FileOutputStream(txtFile, false);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(output);

                    JSONObject obj1 = new JSONObject();
                    obj1.put("students", students);
                    obj1.put("devices", devices);
                    obj1.put("end_time", end_time);
                    obj1.put("start_time", start_time);
                    obj1.put("title", title);

                    String str = obj1.toString();
                    myOutWriter.append(str);
                    myOutWriter.close();

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


    @Override
    public void onResume() {
        super.onResume();
        handleNfcIntent(getIntent());
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

}
