package com.eum.webf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Locale;

public class DocumentActivity extends Activity {
    Intent intent;
    TextView viewNumber;
    TextView viewTitle;
    TextView viewDate;
    WebView viewContents;

    TextToSpeech tts;
    Button testTtsBtn;
    Button nfcWriteBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_document);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        intent = getIntent();

        final int number = intent.getIntExtra("number", 0);
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        final String contents = (String)DataHolder.popDataHolder(intent.getStringExtra("dataId"));
        Log.i("contents",contents);
        final String writer = intent.getStringExtra("writer");
        final String category = intent.getStringExtra("category");

        viewNumber = findViewById(R.id.viewNumber);
        viewTitle = findViewById(R.id.viewTitle);
        viewDate = findViewById(R.id.viewDate);
        viewContents = findViewById(R.id.viewContents);

        testTtsBtn = findViewById(R.id.testTtsBtn);
        nfcWriteBtn = findViewById(R.id.nfcWriteBtn);

        viewNumber.setText(number+"");
        viewTitle.setText(title);
        viewDate.setText(date);
        String encodedHtml = Base64.encodeToString(contents.getBytes(), Base64.NO_PADDING);
        if (Build.VERSION.SDK_INT >= 19) {
            viewContents.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            viewContents.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        viewContents.getSettings().setBuiltInZoomControls(true);
        viewContents.getSettings().setJavaScriptEnabled(true);
        viewContents.loadData(encodedHtml, "text/html", "base64");
        //viewContents.setText(Html.fromHtml(contents));
        //viewContents.setMovementMethod(new ScrollingMovementMethod());

        testTtsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(Html.fromHtml(contents).toString());
                } else {
                    ttsUnder20(Html.fromHtml(contents).toString());
                }
            }
        });

        nfcWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentActivity.this, TagWriteActivity.class);
                intent.putExtra("writer", writer);
                intent.putExtra("category",category);
                intent.putExtra("number",number);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

}
