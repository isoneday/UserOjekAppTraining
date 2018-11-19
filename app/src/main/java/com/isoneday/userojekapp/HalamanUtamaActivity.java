package com.isoneday.userojekapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HalamanUtamaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
    }

    public void onHistory(View view) {
    startActivity(new Intent(this,HistoryActivity.class));
    }


    public void onGoride(View view) {
        startActivity(new Intent(this,GorideActivity.class));

    }
}
