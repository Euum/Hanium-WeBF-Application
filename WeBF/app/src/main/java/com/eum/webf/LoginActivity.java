package com.eum.webf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public class LoginActivity extends AppCompatActivity {
    EditText loginId;
    EditText loginPassword;
    Button loginBtn;
    CheckBox accountCheckBox;
    TextView testText;
    ProgressBar loginProgressBar;

    SharedPreferences account;
    SharedPreferences.Editor account_editor;
    public static Activity _LoginActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        _LoginActivity = LoginActivity.this;

        testText = findViewById(R.id.testText);

        loginId = findViewById(R.id.loginId);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        accountCheckBox = findViewById(R.id.accountCheckBox);

        AsyncHttpClient client = HttpClient.getInstance();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);

        account = getSharedPreferences("webf_account",MODE_PRIVATE);
        account_editor = account.edit();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("버튼","OnClick 작동");
                if(accountCheckBox.isChecked()){
                    account_editor.putString("id",loginId.getText().toString());
                    account_editor.putString("password",loginPassword.getText().toString());
                    account_editor.commit();
                }
                RequestParams params = new RequestParams();
                params.put("id",loginId.getText().toString());
                params.put("password",loginPassword.getText().toString());
                HttpClient.post("mobileLogin", params, new AsyncHttpResponseHandler() {
                    private ProgressDialog progressDialog= new ProgressDialog(LoginActivity.this);
                    @Override
                    public void onStart() {
                        progressDialog.setMessage("로그인 처리중...");
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
                        String responseStr = Utils.bytesToString(responseBody);
                        Log.i("코드",responseStr);
                        testText.setText(responseStr);
                        if(responseStr.equals("login success")||responseStr.equals("aleady login")){
                            Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"올바른 계정이 아닙니다.",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(), "통신 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if(accountCheckBox.isChecked() && !account.getAll().isEmpty()){
            loginId.setText(account.getString("id","FAIL_TYPE"));
            loginPassword.setText(account.getString("password","FAIL_TYPE"));
            loginBtn.callOnClick();
        }


    }
}
