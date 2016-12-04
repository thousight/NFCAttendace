package com.example.markwen.nfcattendance;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

public class StudentActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback {

    EditText studentNameEditText;
    String studentName;
    NfcAdapter nfcAdapter;
    private String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Student");

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
        NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            nfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
            Toast.makeText(this, "NFC available on this device, go ahead and tap on your professor's device.",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "NFC not available on this device, join the iPhone users.",
                    Toast.LENGTH_LONG).show();
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
        NdefRecord[] records = new NdefRecord[2];
        records[0] = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                new byte[0],
                studentName.getBytes());
        records[1] = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                new byte[0],
                android_id.getBytes());
        return records;
    }
}
