package com.isoneday.userojekapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.isoneday.userojekapp.helper.SessionManager;


public class Splash extends AppCompatActivity {

    SessionManager sesi ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sesi = new SessionManager(Splash.this);


        //sebelum pindah ke halaman ke login atau ke mainactivity kita delay

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sesi.isLogin()==true){

                    startActivity(new Intent(Splash.this,HalamanUtamaActivity.class));

                }else {
                    startActivity(new Intent(Splash.this,LoginRegisterActivity.class));

                }
            }
            //waktu delaynya 4000 ms = 4 s
        },3000);


    }
}
