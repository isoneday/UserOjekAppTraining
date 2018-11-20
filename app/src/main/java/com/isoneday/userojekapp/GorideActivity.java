package com.isoneday.userojekapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.isoneday.userojekapp.helper.DirectionMapsV2;
import com.isoneday.userojekapp.helper.GPSTracker;
import com.isoneday.userojekapp.helper.HeroHelper;
import com.isoneday.userojekapp.helper.MyContants;
import com.isoneday.userojekapp.helper.SessionManager;
import com.isoneday.userojekapp.model.Distance;
import com.isoneday.userojekapp.model.Duration;
import com.isoneday.userojekapp.model.LegsItem;
import com.isoneday.userojekapp.model.ResponseInsertBooking;
import com.isoneday.userojekapp.model.ResponseWaypoint;
import com.isoneday.userojekapp.model.RoutesItem;
import com.isoneday.userojekapp.network.InitRetrofit;
import com.isoneday.userojekapp.network.RestApi;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GorideActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.imgpick)
    ImageView imgpick;
    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.edtcatatan)
    EditText edtcatatan;
    @BindView(R.id.txtharga)
    TextView txtharga;
    @BindView(R.id.txtjarak)
    TextView txtjarak;
    @BindView(R.id.txtdurasi)
    TextView txtdurasi;
    @BindView(R.id.requestorder)
    Button requestorder;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private GPSTracker gps;
    private double latawal;
    private double lonawal;
    private String namelocation;
    private LatLng lokasiku;
    private double latakhir;
    private double lonakhir;
    private List<RoutesItem> data;
    private List<LegsItem> legs;
    private Distance distance;
    private Duration duration;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //periksa kondisi gps user
        cekstatusgps();
        session = new SessionManager(this);
    }

    private void cekstatusgps() {
        // cek sttus gps aktif atau tidak
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            //     finish();
        }
        // Todo Location Already on  ... end
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            //menampilkan popup untuk mengaktifkan gps
            enableLoc();
        }
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(GorideActivity.this, MyContants.REQUEST_LOCATION);

                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

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
        gps = new GPSTracker(this);
        //cek permission untuk os marshmellow ke atas
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        110);


            }
            return;
        }else {
            getmylocation();

        }}

    private void getmylocation() {
        if (gps.canGetLocation()) {
            latawal = gps.getLatitude();
            lonawal = gps.getLongitude();
            namelocation = posisiku(latawal, lonawal);
            lokasiawal.setText(namelocation);
            //buat objek untuk mengatur tampilan map
            lokasiku = new LatLng(latawal, lonawal);
            mMap.addMarker(new MarkerOptions().position(lokasiku).title(namelocation))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku,17));
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    //untuk mendapatkan detail alamat dari 2 koordinat (lat dan lon)
    private String posisiku(double latawal, double lonawal) {
        namelocation = null;
        Geocoder geocoder = new Geocoder(GorideActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(latawal, lonawal, 1);
            if (list != null && list.size() > 0) {
                namelocation = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            } else {
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return namelocation;
    }

    @OnClick({R.id.lokasiawal, R.id.lokasitujuan, R.id.requestorder})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lokasiawal:
                setlokasi(MyContants.LOKASIAWAL);
                break;
            case R.id.lokasitujuan:
                setlokasi(MyContants.LOKASITUJUAN);

                break;
            case R.id.requestorder:
                prosesrequest();
                break;
        }
    }

    private void prosesrequest() {
        int iduser = Integer.parseInt(session.getIdUser());
        String token = session.getToken();
        String awal = lokasiawal.getText().toString();
        String akhir = lokasitujuan.getText().toString();
        String ltawal = String.valueOf(latawal);
        String lnawal= String.valueOf(lonawal);
        String ltakhir =String.valueOf(latakhir);
        String lnakhir =String.valueOf(lonakhir);
        String catatan = edtcatatan.getText().toString();
        String device = HeroHelper.getDeviceUUID(this);
        float jarak = Float.parseFloat(HeroHelper.removeLastChar(txtjarak.getText().toString()).trim());

        RestApi api = InitRetrofit.getInstance();
        Call<ResponseInsertBooking> bookingCall =api.insertbooking(
                iduser,
                ltawal,
                lnawal,
                awal,
                ltakhir,
                lnakhir,
                akhir,
                catatan,
                jarak,
                token,
                device
        );
        bookingCall.enqueue(new Callback<ResponseInsertBooking>() {
            @Override
            public void onResponse(Call<ResponseInsertBooking> call, Response<ResponseInsertBooking> response) {
                if (response.isSuccessful())
                {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")){
                        Toast.makeText(GorideActivity.this, msg, Toast.LENGTH_SHORT).show();
                        int idbooking =response.body().getIdBooking();
                        Intent i = new Intent(GorideActivity.this,WaitingDriverActivity.class);
                        i.putExtra(MyContants.IDBOOKING,idbooking);
                        startActivity(i);
                    }else{
                        Toast.makeText(GorideActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseInsertBooking> call, Throwable t) {

            }
        });
    }

    private void setlokasi(int lokasi) {
        AutocompleteFilter filter = new AutocompleteFilter.Builder().
                setCountry("ID")
                .build();

        Intent i = null;
        try {
            i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(GorideActivity.this);
            startActivityForResult(i, lokasi);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    //untuk menangkap response atau result dari startActivityForResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Place p = PlaceAutocomplete.getPlace(this, data);
        if (requestCode == MyContants.LOKASIAWAL && resultCode == RESULT_OK) {
            latawal = p.getLatLng().latitude;
            lonawal = p.getLatLng().longitude;
            LatLng awal = new LatLng(latawal, lonawal);
            mMap.clear();
            namelocation = p.getAddress().toString();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(awal, 17));
            lokasiawal.setText(namelocation);
        }else if (requestCode == MyContants.LOKASITUJUAN && resultCode ==RESULT_OK){
            latakhir = p.getLatLng().latitude;
            lonakhir = p.getLatLng().longitude;
            LatLng akhir = new LatLng(latakhir, lonakhir);
            namelocation = p.getAddress().toString();
            lokasitujuan.setText(namelocation);
            mMap.addMarker(new MarkerOptions().position(akhir).title(namelocation))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup));

            aksesrute();
        }

    }

    private void aksesrute() {
        RestApi api= InitRetrofit.getInstanceGoogle();
        String key = getText(R.string.google_maps_key).toString();
        Call<ResponseWaypoint> waypointCall = api.getrutelokasi(
                lokasiawal.getText().toString(),
                lokasitujuan.getText().toString(),
                key
        );
        waypointCall.enqueue(new Callback<ResponseWaypoint>() {
            @Override
            public void onResponse(Call<ResponseWaypoint> call, Response<ResponseWaypoint> response) {
             if (response.isSuccessful()){
                 String status = response.body().getStatus();
                 if (status.equals("OK")){
                     data = response.body().getRoutes();
                     legs = data.get(0).getLegs();
                        distance = legs.get(0).getDistance();
                        duration = legs.get(0).getDuration();
                        txtdurasi.setText(duration.getText().toString());
                        txtjarak.setText(distance.getText().toString());
                     DirectionMapsV2 mapsV2 = new DirectionMapsV2(GorideActivity.this);
                     String points = data.get(0).getOverviewPolyline().getPoints();
                     mapsV2.gambarRoute(mMap,points);
                 }
             }
            }

            @Override
            public void onFailure(Call<ResponseWaypoint> call, Throwable t) {

            }
        });
    }
}
