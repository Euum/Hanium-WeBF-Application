package com.eum.webf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class CategoryActivity extends AppCompatActivity {

    GridLayout categoryView;
    JSONArray categories;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        setActionBar("카테고리");

        LoginActivity._LoginActivity.finish();

        categoryView = findViewById(R.id.categoryView);
        layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        HttpClient.get("mobileCategory", new RequestParams(),new AsyncHttpResponseHandler() {
            private ProgressDialog progressDialog= new ProgressDialog(CategoryActivity.this);
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
                Log.i("BODY", "" + Utils.bytesToString(responseBody));
                try{
                    categories = Utils.bytesToJsonArray(responseBody);
                    for (int i = 0; i < categories.length(); i++) {
                        Button categoryButton = (Button) layoutInflater.inflate(R.layout.category_button, categoryView, false);
                        final String categoryTitle = categories.getJSONObject(i).getString("categoryTitle");
                        categoryButton.setText(categoryTitle);
                        categoryButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CategoryActivity.this, DocListActivity.class);
                                intent.putExtra("categoryTitle", categoryTitle);
                                intent.putExtra("page", 0);
                                startActivity(intent);
                            }
                        });
                        categoryButton.setSelected(true);
                        categoryView.addView(categoryButton);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"통신 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutSelected(){
        HttpClient.get("mobileLogout", new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(),Utils.bytesToString(responseBody),Toast.LENGTH_SHORT).show();
                SharedPreferences account = getSharedPreferences("webf_account",MODE_PRIVATE);
                SharedPreferences.Editor account_editor = account.edit();
                account_editor.clear();
                account_editor.commit();
                Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"통신 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setActionBar(String text){
        CustomActionBar ca = new CustomActionBar(this, getSupportActionBar());
        ca.setActionBar(text)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutSelected();
                    }
                });
    }
}
