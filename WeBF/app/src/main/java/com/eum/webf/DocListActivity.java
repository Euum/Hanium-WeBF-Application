package com.eum.webf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class DocListActivity extends AppCompatActivity {
    int PER_PAGE = 10;
    static int PAGE;
    static int PAGE_COUNT;
    TableLayout doc_table;
    JSONObject response;
    JSONArray documents;
    LayoutInflater layoutInflater;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        intent = getIntent();

        setActionBar(intent.getStringExtra("categoryTitle"));

        doc_table = findViewById(R.id.doc_table);
        layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        PAGE = 0;
        PAGE_COUNT=0;
        request();
    }

    private  void refresh(){
        doc_table.removeAllViews();
        request();
    }

    public void request(){

        RequestParams params = new RequestParams();
        params.put("categoryTitle", intent.getStringExtra("categoryTitle"));
        params.put("page",PAGE);
        params.put("perPage",PER_PAGE);

        HttpClient.get("mobileDocList", params, new AsyncHttpResponseHandler() {
            private ProgressDialog progressDialog= new ProgressDialog(DocListActivity.this);
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
            public void onSuccess(final int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = Utils.bytesToJsonObject(responseBody);
                    documents = response.getJSONArray("documents");
                    PAGE_COUNT = response.getInt("pageCount");

                    for(int i=0; i<documents.length(); i++){
                        JSONObject currentObj = documents.getJSONObject(i);
                        final int number = currentObj.getInt("number");
                        final String title = currentObj.getString("title");
                        Date created_at = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(currentObj.getString("created_at"));
                        final String date = new SimpleDateFormat("yy년MM월dd일").format(created_at);
                        final String contents = currentObj.getString("contents");
                        final String writer = currentObj.getJSONObject("writer").getString("_id");
                        final String category = currentObj.getString("category");
                        Log.i("DOC","number:"+number+", title:"+title+", date:"+created_at);

                        TableRow table_row = (TableRow)layoutInflater.inflate(R.layout.table_row, doc_table, false);
                        TextView doc_title = table_row.findViewById(R.id.doc_title);
                        TextView doc_number = table_row.findViewById(R.id.doc_number);
                        TextView doc_date = table_row.findViewById(R.id.doc_date);

                        doc_title.setText(title);
                        doc_title.setSelected(true);
                        doc_number.setText(""+number);
                        doc_number.setSelected(true);
                        doc_date.setText(date);
                        doc_date.setSelected(true);

                        table_row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DocListActivity.this, DocumentActivity.class);
                                intent.putExtra("number",number);
                                intent.putExtra("title",title);
                                intent.putExtra("date",date);
                                String dataId = DataHolder.putDataHolder(contents);
                                intent.putExtra("dataId", dataId);
                                intent.putExtra("category",category);
                                intent.putExtra("writer",writer);
                                startActivityForResult(intent, 1);
                            }
                        });
                        doc_table.addView(table_row);
                    }
                    Log.i("JSON", response.toString());
                    TextView pageIndicator = findViewById(R.id.pageIndicator);
                    pageIndicator.setText((PAGE+1)+"/"+(PAGE_COUNT));
                    Button leftBtn = findViewById(R.id.leftBtn);
                    Button rightBtn = findViewById(R.id.rightBtn);

                    leftBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(PAGE > 0){
                                PAGE--;
                                refresh();
                            }
                        }
                    });

                    rightBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(PAGE < PAGE_COUNT-1 ){
                                PAGE++;
                                refresh();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"통신 실패",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(DocListActivity.this, MainActivity.class);
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
