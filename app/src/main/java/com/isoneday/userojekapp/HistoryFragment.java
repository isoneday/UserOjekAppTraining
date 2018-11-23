package com.isoneday.userojekapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.isoneday.userojekapp.helper.CustomRecycler;
import com.isoneday.userojekapp.helper.HeroHelper;
import com.isoneday.userojekapp.helper.SessionManager;
import com.isoneday.userojekapp.model.DataHistory;
import com.isoneday.userojekapp.model.ResponseHistory;
import com.isoneday.userojekapp.network.InitRetrofit;
import com.isoneday.userojekapp.network.RestApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    Unbinder unbinder;
    private SessionManager session;
    private String iduser;
    private String token;
    private String device;
    private List<DataHistory> datahistory;
    int idstatus ;

    @SuppressLint("ValidFragment")
    public HistoryFragment(int i) {
        idstatus =i;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tampilan = inflater.inflate(R.layout.fragment_proses, null);
        unbinder = ButterKnife.bind(this, tampilan);
        session = new SessionManager(getActivity());

        getdatahistory();
        return tampilan;
    }

    private void getdatahistory() {
        iduser =session.getIdUser();
        token =session.getToken();
        device = HeroHelper.getDeviceUUID(getContext());

        RestApi api = InitRetrofit.getInstance();
        Call<ResponseHistory> historyCall  =api.getdatahistory(Integer.parseInt(iduser)
                ,token,device,idstatus);
        historyCall.enqueue(new Callback<ResponseHistory>() {
            @Override
            public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                if (response.isSuccessful()){
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")){
                        datahistory = response.body().getData();
                        CustomRecycler adapter = new CustomRecycler(datahistory,getActivity());
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }else{
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHistory> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
