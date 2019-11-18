package com.eum.webf;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class TagWriteActivity extends AppCompatActivity {
    Intent intent;
    TextView writeState;

    final int TYPE_TEXT = 1;
    final int TYPE_URI = 2;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    int number;
    String category;
    String writer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagwrite);

        writeState = findViewById(R.id.writeState);

        //Write Sequence => "writer/category/number"
        number = getIntent().getIntExtra("number",1);
        category = getIntent().getStringExtra("category");
        writer = getIntent().getStringExtra("writer");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent == null){
            return;
        }

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        try {
            writeTag(getTextAsNdef(), detectedTag);
            writeState.setText("기록 완료");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter != null){
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,null,null);
        }
    }

    private NdefMessage getTextAsNdef(){
        String inputData = writer+"/"+category+"/"+number;
        byte[] textBytes = inputData.getBytes();

        NdefRecord applicationRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "application/com.eum.webf".getBytes(),
                new byte[]{},
                new byte[]{});

        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[]{},
                textBytes);

        return new NdefMessage(new NdefRecord[]{applicationRecord, textRecord});
    }

    private void writeTag(NdefMessage msg, Tag tag) throws IOException, FormatException{
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(msg);
        ndef.close();
    }
}
