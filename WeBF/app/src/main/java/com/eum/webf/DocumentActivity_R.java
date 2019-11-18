package com.eum.webf;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class DocumentActivity_R extends AppCompatActivity {
    WebView viewContents_read;
    Button ttsAgainBtn;
    TextToSpeech tts;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_r);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        boolean isAccessibilityEnabled = am.isEnabled();
        final boolean isExploreByTouchEnabled = am.isTouchExplorationEnabled();

        viewContents_read = findViewById(R.id.viewContents_read);
        ttsAgainBtn = findViewById(R.id.ttsAgainBtn);

        final String contents = (String)DataHolder.popDataHolder(getIntent().getStringExtra("dataId"));

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
                if(!isExploreByTouchEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ttsGreater21(Html.fromHtml(contents).toString());
                    } else {
                        ttsUnder20(Html.fromHtml(contents).toString());
                    }
                }
            }
        });

        String encodedHtml = Base64.encodeToString(contents.getBytes(), Base64.NO_PADDING);
        if (Build.VERSION.SDK_INT >= 19) {
            viewContents_read.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            viewContents_read.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        viewContents_read.getSettings().setBuiltInZoomControls(true);
        viewContents_read.getSettings().setJavaScriptEnabled(true);
        viewContents_read.loadData(encodedHtml, "text/html", "base64");

        ttsAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(Html.fromHtml(contents).toString());
                } else {
                    ttsUnder20(Html.fromHtml(contents).toString());
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        //TagReadActivity._TagReadActivity.finish();
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
        Log.i("tts","start");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        Log.i("tts","start");
    }
}
