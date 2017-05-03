package com.marat.apps.android.pro3.Internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetRequest {

    private Context context;
    private Call call;
    private boolean callerAlive;

    public RequestResponseListener delegate = null;

    public GetRequest(Context c) {
        context = c;
        callerAlive = true;
    }

    public void get(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    public void getUserInfo(String url, String token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    public void getCarWashersForCityId(String url, String token, int cityId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .addHeader("City", cityId + "")
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    public void getFavoriteCarWashersForUserId(String url, String token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    public void getCarWash(String url, String token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    public void getTimetableOfCarWash(String url, String token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    public void getAllUserOrders(String url, String token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    private void executeCall() {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callerAlive) {
                    delegate.onFailure(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (callerAlive) {
                    delegate.onResponse(response);
                }
            }
        });
    }

    public void cancelCall() {
        callerAlive = false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
