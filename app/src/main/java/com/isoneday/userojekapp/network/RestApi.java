package com.isoneday.userojekapp.network;


import com.isoneday.userojekapp.model.ResponseCheckBooking;
import com.isoneday.userojekapp.model.ResponseInsertBooking;
import com.isoneday.userojekapp.model.ResponseLoginRegis;
import com.isoneday.userojekapp.model.ResponseWaypoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {
//    //todo 2 set endpoint di api.php

    //    //endpoint untuk register
    @FormUrlEncoded
    @POST("daftar/2")
    Call<ResponseLoginRegis> registeruser(
            @Field("nama") String nama,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("email") String email
    );

    //
    //endpoint untuk login
    @FormUrlEncoded
    @POST("login")
    Call<ResponseLoginRegis> loginuser(
            @Field("device") String device,
            @Field("f_password") String pass,
            @Field("f_email") String email);
//
    //endpoint untuk login
    @FormUrlEncoded
    @POST("insert_booking")
    Call<ResponseInsertBooking> insertbooking(
            @Field("f_idUser") int iduser,
            @Field("f_latAwal") String latawal,
            @Field("f_lngAwal") String longawal,
            @Field("f_awal") String awal,
            @Field("f_latAkhir") String latakhir,
            @Field("f_lngAkhir") String longakhir,
            @Field("f_akhir") String lokasitujuan,
            @Field("f_catatan") String catatan,
            @Field("f_jarak") float jarak,
            @Field("f_token") String token,
            @Field("f_device") String device
    );
//
//
    //endpoint untuk checkbooking
    @FormUrlEncoded
    @POST("checkBooking")
    Call<ResponseCheckBooking> checkbooking(
            @Field("idbooking") int idbooking);
//
//    //endpoint untuk getdata history
//    @FormUrlEncoded
//    @POST("get_booking")
//    Call<ResponseHistory> getdatahistory(
//            @Field("f_idUser") int iduser,
//            @Field("f_token") String token,
//            @Field("f_device") String device,
//            @Field("status") int status);
//
//    //endpoint untuk chancelbooking
//    @FormUrlEncoded
//    @POST("cancel_booking")
//    Call<ResponseRegister> cancelbooking(
//            @Field("idbooking") int idbooking,
//            @Field("f_token") String token,
//            @Field("f_device") String device);
//
    //endpoint ke api google map waypoint
    @GET("json")
    Call<ResponseWaypoint> getrutelokasi(
            @Query("origin") String alamatasal,
            @Query("destination") String alamattujuan,
            @Query("key") String key
    );
//
//    //endpoint untuk login
//    @FormUrlEncoded
//    @POST("get_driver")
//    Call<ResponseDetailDriver> getdetaildriver(
//            @Field("f_iddriver") int iddriver);
}