package com.eum.webf;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
    public static String bytesToString(byte[] bytes){
        try{
            return new String(bytes,"UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static JSONArray bytesToJsonArray(byte[] bytes){
        try{
            JSONArray jsonArray = new JSONArray(bytesToString(bytes));
            return jsonArray;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject bytesToJsonObject(byte[] bytes){
        try{
            return new JSONObject(bytesToString(bytes));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static final String CHARS = "0123456789ABCDEF";
    public static String toHexString(byte[] data){
        StringBuffer sb = new StringBuffer();
        for(int i =0; i<data.length;i++){
            sb.append(CHARS.charAt((data[i] >> 4)& 0x0F)).append(
                    CHARS.charAt(data[i] & 0x0F)
            );
        }
        return sb.toString();
    }
}