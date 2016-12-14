package com.example.markwen.nfcattendance;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class StudentActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback {

    EditText studentNameEditText;
    String studentName;
    NfcAdapter nfcAdapter;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        setTitle("Student");

        // Get device ID
        deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        studentNameEditText = (EditText) findViewById(R.id.studentNameEditText);
        studentNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                studentName = editable.toString();
            }
        });
        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        nfcAdapter = manager.getDefaultAdapter();
        if (nfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            nfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
            Snackbar.make(findViewById(R.id.studentNameEditText), "NFC available on this device, go ahead and tap on your professor's device.", Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(findViewById(R.id.studentNameEditText), "NFC not available on this device.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        NdefRecord[] recordsToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {

    }

    private NdefRecord[] createRecords() {
        // Adding information into the message to be sent
        NdefRecord[] records = new NdefRecord[2];
        // Student name
        records[0] = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                new byte[0],
                studentName.getBytes());
        // Device ID
        records[1] = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                new byte[0],
                deviceID.getBytes());
        return records;
    }
}
