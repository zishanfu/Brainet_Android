package com.susanfu.mainActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by SammiFu on 20/11/2017.
 */

public class Home extends Activity{
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "Welcome " + session.getUserDetails().get(SessionManager.KEY_USERNAME).toString(), Toast.LENGTH_LONG).show();

        final Button btnLogin = (Button) findViewById(R.id.logoutButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                session.logoutUser();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
