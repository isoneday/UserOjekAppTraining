package com.isoneday.userojekapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.isoneday.userojekapp.helper.MyContants;
import com.isoneday.userojekapp.helper.SessionManager;
import com.isoneday.userojekapp.model.ResponseCheckBooking;
import com.isoneday.userojekapp.network.InitRetrofit;
import com.isoneday.userojekapp.network.RestApi;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitingDriverActivity extends AppCompatActivity {

    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;
    @BindView(R.id.buttoncancel)
    Button buttoncancel;
    private int idbooking;
    private SessionManager session;
    private String iddriver;
    private Timer time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_driver);
        ButterKnife.bind(this);
        pulsator.start();
        idbooking = getIntent().getIntExtra(MyContants.IDBOOKING,0);
        session = new SessionManager(this);
        cekstatusbooking();
        time = new Timer();
    }

    private void cekstatusbooking() {
        RestApi api = InitRetrofit.getInstance();
        Call<ResponseCheckBooking> bookingCall =api.checkbooking(idbooking);
        bookingCall.enqueue(new Callback<ResponseCheckBooking>() {
            @Override
            public void onResponse(Call<ResponseCheckBooking> call, Response<ResponseCheckBooking> response) {
                if (response.isSuccessful()){
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")){
                        Toast.makeText(WaitingDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(WaitingDriverActivity.this,DetailDriverAcitivty.class);
                        iddriver = response.body().getDriver();
                        i.putExtra(MyContants.IDDRIVER,iddriver);
                        startActivity(i);
                    }else{
                        Toast.makeText(WaitingDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCheckBooking> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                cekstatusbooking();
            }
        },0,3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        time.cancel();
    }

    @OnClick(R.id.buttoncancel)
    public void onViewClicked() {

    }
}
