package com.susanfu.mainActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.susanfu.web.WebService;

import java.util.concurrent.ExecutionException;


public class RegisterActivity extends AppCompatActivity  {

    private String info;
    private TextView regtv;
    private Context context;

    private AutoCompleteTextView mUsernameView;
    private String ipAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        regtv = (TextView) findViewById(R.id.info);
        context = getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAdress = extras.getString("ip");
        }

        Button mRegisterInButton = (Button) findViewById(R.id.user_register_in_button);
        mRegisterInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    private void attemptRegister() {
        mUsernameView.setError(null);
        if(getSignalStr().length() > 20000){
            WebService wb = new WebService("/user/signup", mUsernameView.getText().toString(), getSignalStr(), context, ipAdress);
            //new Thread(new MyThread()).start();
            try {
                info = wb.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            regtv.setText(info);
        }else{
            regtv.setText("Please enter a valid username!");
        }
    }

    public String getSignalStr(){
        return Tools.getSignal(mUsernameView.getText().toString());
    }


}

