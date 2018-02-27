package com.susanfu.mainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.susanfu.web.WebService;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private String info;
    private TextView infotv;
    private AutoCompleteTextView mUsernameView;

    private SessionManager session;
    private Context context;
    private String ipAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        infotv = (TextView) findViewById(R.id.info);
        session = new SessionManager(getApplicationContext());
        context = getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAdress = extras.getString("ip");
        }

        Button mRegisterInButton = (Button) findViewById(R.id.user_register_in_button);
        if (mRegisterInButton != null) {
            mRegisterInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent regItn = new Intent(LoginActivity.this, RegisterActivity.class);
                    regItn.putExtra("ip", ipAdress);
                    startActivity(regItn);
                }
            });
        }

        if(session.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), Home.class));
        }

        Button mSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        if (mSignInButton != null) {
            mSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }
    }

    private void attemptLogin() {
        mUsernameView.setError(null);
        if(getSignalStr().length() > 20000){
            WebService wb = new WebService("/login",mUsernameView.getText().toString(), getSignalStr(), context, ipAdress);
            try {
                info = wb.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            infotv.setText(info);
        }else{
            infotv.setText("Please enter a valid username!");
        }
    }


    public String getSignalStr(){
        return Tools.getSignal(mUsernameView.getText().toString());
    }

}

