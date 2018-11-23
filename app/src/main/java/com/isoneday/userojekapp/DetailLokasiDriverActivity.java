package com.isoneday.userojekapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.isoneday.userojekapp.helper.MyContants;
import com.isoneday.userojekapp.helper.SessionManager;
import com.isoneday.userojekapp.model.DataDetailDriver;
import com.isoneday.userojekapp.model.ResponseDetailDriver;
import com.isoneday.userojekapp.network.InitRetrofit;
import com.isoneday.userojekapp.network.RestApi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailLokasiDriverActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.txtnamadriver)
    TextView txtnamadriver;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.txthpdriver)
    TextView txthpdriver;
    @BindView(R.id.linear1)
    LinearLayout linear1;
    private GoogleMap mMap;
    private String id;
    private List<DataDetailDriver> datadriver;
    private double latdriver;
    private double londriver;
    private LatLng posisidriver;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lokasi_driver);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        id = getIntent().getStringExtra(MyContants.IDDRIVER);
        session = new SessionManager(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        getdetaildriver(mMap);

    }

    private void getdetaildriver(GoogleMap mMap) {
        final ProgressDialog dialog = ProgressDialog.show(this, "proses get detail driver"
                , "loading . . .");
        RestApi api = InitRetrofit.getInstance();
        Call<ResponseDetailDriver> driverCall = api.getdetaildriver(Integer.parseInt(id));
        driverCall.enqueue(new Callback<ResponseDetailDriver>() {
            @Override
            public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                if (response.isSuccessful()) {
                        String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    dialog.dismiss();
                    if (result.equals("true")) {
                        Toast.makeText(DetailLokasiDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        datadriver = response.body().getData();
                        txtnamadriver.setText(datadriver.get(0).getUserNama());
                        txthpdriver.setText(datadriver.get(0).getUserHp());

                        //set map information
                        latdriver = Double.parseDouble(datadriver.get(0).getTrackingLat());
                        londriver = Double.parseDouble(datadriver.get(0).getTrackingLng());
                        posisidriver = new LatLng(latdriver, londriver);
                        DetailLokasiDriverActivity.this.mMap.addMarker(new MarkerOptions().position(posisidriver))
                                .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car));
                        DetailLokasiDriverActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posisidriver, 17));
                        //padding maps
                        DetailLokasiDriverActivity.this.mMap.setPadding(40, 150, 50, 120);
                        // menampilkan compas
                        DetailLokasiDriverActivity.this.mMap.getUiSettings().setCompassEnabled(true);
                        DetailLokasiDriverActivity.this.mMap.getUiSettings().setZoomControlsEnabled(true);
                        DetailLokasiDriverActivity.this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    } else {
                        Toast.makeText(DetailLokasiDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseDetailDriver> call, Throwable t) {
                Toast.makeText(DetailLokasiDriverActivity.this, "masalah koneksi", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.txthpdriver)
    public void onViewClicked() {
        if (ActivityCompat.checkSelfPermission(DetailLokasiDriverActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + datadriver.get(0).getUserHp())));

    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailLokasiDriverActivity.this);
        builder.setTitle("pilihan ?");
        builder.setPositiveButton("history", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(DetailLokasiDriverActivity.this, HistoryBookingActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(DetailLokasiDriverActivity.this, HalamanUtamaActivity.class));
                finish();
            }
        });
        builder.show();
    }

    Timer autoUpdate;

    public void onResume() {
        super.onResume();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {

                        getdetaildriver(mMap);


                        //RbHelper.pesan(c,"ngulang");


                    }
                });
            }
        }, 0, 3000); // updates each 40 secs
    }

    @Override
    protected void onPause() {
        autoUpdate.cancel();
        super.onPause();
    }
}
