package com.eum.webf;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class TagReadOutProcess extends Activity {
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if(nfcAdapter == null){
            Toast.makeText(getApplicationContext(), "NFC 미지원 단말기 입니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onPause() {
        if(nfcAdapter != null){
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this,pendingIntent,null,null);
        }
        getNFCData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        getNFCData(intent);
    }

    private void getNFCData(Intent intent){
        String receiveData;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs == null) Log.i("rawMsgs", "null");
            if (rawMsgs != null) {
                NdefMessage[] messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
                byte[] payload = messages[0].getRecords()[1].getPayload();
                receiveData = new String(payload);

                String[] items = receiveData.split("/");
                String writer;
                String category;
                String number;
                if(items.length == 3){
                    writer = items[0];
                    category = items[1];
                    number = items[2];
                }else{
                    return;
                }
                RequestParams params = new RequestParams();
                params.put("writer",writer);
                params.put("category",category);
                params.put("number",Integer.parseInt(number));
                HttpClient.post("mobileReadDoc", params, new AsyncHttpResponseHandler() {
                    private ProgressDialog progressDialog= new ProgressDialog(TagReadOutProcess.this);
                    @Override
                    public void onStart() {
                        progressDialog.setMessage("불러오는 중...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        super.onStart();
                    }

                    @Override
                    public void onFinish() {
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        super.onFinish();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Intent intent = new Intent(TagReadOutProcess.this, DocumentActivity_R.class);
                        String dataId = DataHolder.putDataHolder(Utils.bytesToString(responseBody));
                        intent.putExtra("dataId", dataId);
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        startActivityForResult(intent, 1);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(), "통신 실패", Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        finish();
                    }
                });

            }
        }
    }
}
