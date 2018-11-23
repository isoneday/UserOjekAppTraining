package com.isoneday.userojekapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isoneday.userojekapp.helper.HeroHelper;
import com.isoneday.userojekapp.helper.SessionManager;
import com.isoneday.userojekapp.model.DataUser;
import com.isoneday.userojekapp.model.ResponseLoginRegis;
import com.isoneday.userojekapp.network.InitRetrofit;
import com.isoneday.userojekapp.network.RestApi;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginRegisterActivity extends AppCompatActivity {

    @BindView(R.id.txt_rider_app)
    TextView txtRiderApp;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private SessionManager session;
    private DataUser data;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo 2 generate butterknife
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                    ) {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        110);


            }
            return;
        }
    }

    @OnClick({R.id.btnSignIn, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                login();
                break;
            case R.id.btnRegister:
                //todo 3 tampilkan layout register
                register();
                break;
        }
    }

    private void login() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setMessage(R.string.messageregister);
        //layaanan yang membuat sebuah dalam bentuk popup
        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilanlogin = inflater.inflate(R.layout.layout_login, null);
        final ViewHolderLogin login = new ViewHolderLogin(tampilanlogin);
        builder.setView(tampilanlogin);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//todo 4 cek validasi
                if (TextUtils.isEmpty(login.edtEmail.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requireemail, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(login.edtPassword.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirepassword, Snackbar.LENGTH_SHORT).show();
                } else if (login.edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootlayout, R.string.minimumpassword, Snackbar.LENGTH_SHORT).show();
                } else {
                    //todo 5 proses ke api
                    proseslogin(dialogInterface, login);
                }

            }
        });
        //untuk menampilkan dialog
        builder.show();
    }


    //popup
    private void register() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage(R.string.messageregister);
        //layaanan yang membuat sebuah dalam bentuk popup
        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilanregister = inflater.inflate(R.layout.layout_register, null);
        final ViewHolderRegister register = new ViewHolderRegister(tampilanregister);
        builder.setView(tampilanregister);
        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//todo 4 cek validasi
                if (TextUtils.isEmpty(register.edtEmail.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requireemail, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(register.edtPassword.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirepassword, Snackbar.LENGTH_SHORT).show();
                } else if (register.edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootlayout, R.string.minimumpassword, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(register.edtName.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirename, Snackbar.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(register.edtPhone.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirephone, Snackbar.LENGTH_SHORT).show();
                } else {
                    //todo 5 proses ke api
                    prosesregister(dialogInterface, register);
                }

            }
        });
        //untuk menampilkan dialog
        builder.show();
    }

    private void proseslogin(final DialogInterface dialogInterface, ViewHolderLogin login) {
//tampilkan progress dialog / widget loading
        final ProgressDialog dialog = ProgressDialog.show(this, "Prores login", "loading . . .");
        //get instance dari retrofit
        RestApi api = InitRetrofit.getInstance();
        String device = HeroHelper.getDeviceUUID(this);
        //request ke api
        Call<ResponseLoginRegis> loginuser = api.loginuser(
                device,
                login.edtPassword.getText().toString(),
                login.edtEmail.getText().toString()
        );

        loginuser.enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                if (response.isSuccessful()) {
                    String rest = response.body().getResult();
                    String msg = response.body().getMsg();
                    session = new SessionManager(LoginRegisterActivity.this);
                    dialog.dismiss();
                    if (rest.equals("true")) {
                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginRegisterActivity.this, HalamanUtamaActivity.class));
                        data = response.body().getData();
                        session.setEmail(data.getUserEmail());
                        session.setIduser(data.getIdUser());
                        token = response.body().getToken();
                        session.createLoginSession(token);
                            //dialog register hilang ketika sukses
                    } else {

                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                    dialogInterface.dismiss();

                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
dialog.dismiss();
dialogInterface.dismiss();
                Toast.makeText(LoginRegisterActivity.this, "cek koneksi anda"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prosesregister(final DialogInterface dialogInterface, ViewHolderRegister register) {
        //tampilkan progress dialog / widget loading
        final ProgressDialog dialog = ProgressDialog.show(this, "Prores register", "loading . . .");
        //get instance dari retrofit
        RestApi api = InitRetrofit.getInstance();
        //request ke api
        Call<ResponseLoginRegis> regisCall = api.registeruser(
                register.edtName.getText().toString(),
                register.edtPassword.getText().toString(),
                register.edtPhone.getText().toString(),
                register.edtEmail.getText().toString()
        );
        //tangkap callback
        regisCall.enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                if (response.isSuccessful()) {
                    String rest = response.body().getResult();
                    String msg = response.body().getMsg();
                    dialog.dismiss();
                    if (rest.equals("true")) {
                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        //dialog register hilang ketika sukses
                    } else {

                        Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                    dialogInterface.dismiss();

                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
                dialogInterface.dismiss();
                Toast.makeText(LoginRegisterActivity.this, "cek koneksi anda" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    static class ViewHolderRegister {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;
        @BindView(R.id.edtName)
        MaterialEditText edtName;
        @BindView(R.id.edtPhone)
        MaterialEditText edtPhone;

        ViewHolderRegister(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolderLogin {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;

        ViewHolderLogin(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));


    }
}
