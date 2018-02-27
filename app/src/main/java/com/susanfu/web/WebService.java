package com.susanfu.web;

/**
 * Created by SammiFu on 18/11/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebService extends AsyncTask<Void, Void, String> {
    private static String IP;
    private static long AfterTime;
    private static long BeforeTime;

    private String url;
    private String username;
    private String password;
    private Context context;
    private String ip;

    public WebService(String url, String username, String password, Context context, String ip){
        this.url = url;
        this.username = username;
        this.password = password;
        this.context = context;
        this.ip = ip;

    }

    @Override
    protected String doInBackground(Void... voids) {

        HttpURLConnection conn = null;
        InputStream is = null;
        IP = ip;

        BeforeTime = System.currentTimeMillis();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int beforelevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        try {
            String path = "http://" + IP;
            path = path + url;


            conn = (HttpURLConnection) new URL(path).openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) { conn.setRequestProperty("Connection", "close"); }
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);

            Log.i("JSON", jsonObject.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonObject.toString());

            os.flush();
            os.close();


            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());

            AfterTime = System.currentTimeMillis();
            int afterlevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Long TimeDifference = AfterTime - BeforeTime;
            float batteryPct = (beforelevel-afterlevel) / (float)scale;

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                String output = parseInfo(is);
                int index = output.indexOf("res");
                String str= output.length() == 0? "Login Success!": output.substring(index + 5, index + 9);
                String res = str.equals("true")? "Success": str.equals("Login Success!")? str:"";
                return "Execution times: " + TimeDifference + "ms\nPower consumption: " + batteryPct + "\nStatus: " + res;
            }
            return "Fail, please try again!\nExecution times: " + TimeDifference + "ms\nPower consumption: " + batteryPct;

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "Connection Fail!";
    }

    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        return new String(data, "UTF-8");
    }

    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}
