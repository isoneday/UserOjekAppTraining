package com.isoneday.userojekapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.isoneday.userojekapp.helper.SessionManager;

public class HalamanUtamaActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
        session = new SessionManager(this);
    }

    public void onHistory(View view) {
    startActivity(new Intent(this,HistoryBookingActivity.class));
    }


    public void onGoride(View view) {
        startActivity(new Intent(this,GorideActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //untuk memilih item yang ada di menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.history) {
            startActivity(new Intent(this, HistoryBookingActivity.class));
        } else if (id == R.id.profil) {

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Keluar ?");
            builder.setMessage("apakah anda yakin logout aplikasi ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    session.logout();
                    startActivity(new Intent(HalamanUtamaActivity.this, LoginRegisterActivity.class));
                    finish();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
