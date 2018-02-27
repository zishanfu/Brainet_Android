package com.susanfu.mainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.susanfu.web.WebService;

import java.util.concurrent.ExecutionException;

public class SelectActivity extends AppCompatActivity {
    public Context context;
    public String fogIP = "10.0.0.14:8080"; //default setting for fog server is same with cloud server
    public String cloudIP = "34.227.31.40:8080";
    public TextView testTv;
    public int fogTime;
    public int cloudTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Button mFogButton = (Button) findViewById(R.id.fogbtn);
        Button mCloudButton = (Button) findViewById(R.id.cloudbtn);
        final Button mTestButton = (Button) findViewById(R.id.testbtn);
        Button mVideoButton = (Button) findViewById(R.id.videobtn);
        testTv = (TextView) findViewById(R.id.testRes);
        context = getApplicationContext();

        mFogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.putExtra("ip",fogIP);
                startActivity(i);
            }
        });

        mCloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.putExtra("ip",cloudIP);
                startActivity(i);
            }
        });

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String testRes = doTest();
                    showRecDialog(testRes);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), VideoActivity.class);
                startActivity(i);
            }
        });

    }

    public void showRecDialog(String testRes){
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Adaptive offloading")
                .setNegativeButton("Cancel", null).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        if(fogTime < cloudTime){
                            i.putExtra("ip",fogIP);
                        }else{
                            i.putExtra("ip",cloudIP);
                        }
                        startActivity(i);
                    }
                })
                .setMessage(testRes).create();
        dialog.show();
    }

    public String doTest() throws InterruptedException{
        //fog server

        String fogStr = testInDiffServer(fogIP);

        //cloud server
        String cloudStr = testInDiffServer(cloudIP);
        fogTime = Integer.parseInt(fogStr.substring(fogStr.indexOf("time: ") + 6, fogStr.indexOf("ms")));
        cloudTime = Integer.parseInt(cloudStr.substring(cloudStr.indexOf("time: ") + 6, cloudStr.indexOf("ms")));
        String offloading = "";
        if(fogTime < cloudTime){
            offloading = "FogServer";
        }else{
            offloading = "CloudServer";
        }
        String serverResults = "FogServer: " + fogStr + "\nCloudServer: " +  cloudStr +
                "\nRecommendation: " + offloading;

        testTv.setText(serverResults);

        return serverResults;
    }

    public String testInDiffServer(String serverIp) throws InterruptedException{
        int pass = 0;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        long BeforeTime = System.currentTimeMillis();
        int beforelevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        for(int i = 1; i<11; i++){
            String username = i==10?"S010":"S00"+i;
            WebService wb1 = new WebService("/user/signup", username, Tools.getSignal(username), context, serverIp);
            String info1 = null;
            String info2 = null;
            try {
                AsyncTask wbe1 = wb1.execute();
                info1 = wbe1.get().toString();
                Log.d("info1", info1);
                if(i == 10){
                    wbe1.cancel(true);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            WebService wb2 = new WebService("/login", username, Tools.getSignal(username), context, serverIp);

            try {
                AsyncTask wbe2 = wb2.execute();
                info2 = wbe2.get().toString();
                Log.d("info2", info2);
                if(i == 10){
                    wbe2.cancel(true);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(!info1.contains("Fail") && !info2.contains("Fail")){
                pass++;
            }
        }

        long AfterTime = System.currentTimeMillis();
        int afterlevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        Long TimeDifference = AfterTime - BeforeTime;
        float batteryPct = (beforelevel-afterlevel) / (float)scale;

        String res = pass + "/10, Execution time: " + TimeDifference + "ms, power consumption change: " + batteryPct*1000;
        return res;
    }

}
