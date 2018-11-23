package com.isoneday.userojekapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.isoneday.userojekapp.helper.HeroHelper;
import com.isoneday.userojekapp.helper.MyContants;
import com.isoneday.userojekapp.helper.SessionManager;
import com.isoneday.userojekapp.model.ResponseCheckBooking;
import com.isoneday.userojekapp.model.ResponseLoginRegis;
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
    private String token;
    private String device;

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
                        Intent i = new Intent(WaitingDriverActivity.this,DetailLokasiDriverActivity.class);
                        iddriver = response.body().getDriver();
                        i.putExtra(MyContants.IDDRIVER,iddriver);
                        startActivity(i);
                        finish();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel ");
        builder.setMessage("Apakah anda yakin untuk cancel orderan ini ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               RestApi api = InitRetrofit.getInstance();
               token =session.getToken();
               device = HeroHelper.getDeviceUUID(WaitingDriverActivity.this);
               Call<ResponseLoginRegis> regisCall = api.cancelbooking(idbooking,token,device);
               regisCall.enqueue(new Callback<ResponseLoginRegis>() {
                   @Override
                   public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                       if (response.isSuccessful()){
                           String result = response.body().getResult();
                           String msg = response.body().getMsg();
                    if (result.equals("true")){
                        Toast.makeText(WaitingDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(WaitingDriverActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                       }
                   }

                   @Override
                   public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {

                   }
               });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }
}
