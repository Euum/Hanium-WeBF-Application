package com.eum.webf;
import android.app.ProgressDialog;

import com.loopj.android.http.*;
public class HttpClient {
    private static final String BASE_URL = "http://ec2-54-180-32-151.ap-northeast-2.compute.amazonaws.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static AsyncHttpClient getInstance(){
        return HttpClient.client;
    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
    private  static String getAbsoluteUrl(String relativeUrl){
        return BASE_URL + relativeUrl;
    }
    public String getBaseURL(){
        return BASE_URL;
    }
}
