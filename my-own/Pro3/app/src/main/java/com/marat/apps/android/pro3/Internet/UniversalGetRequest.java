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

public class UniversalGetRequest {

    private Context context;
    private Call call;

    public RequestResponseListener delegate = null;

    public UniversalGetRequest(Context c) {
        context = c;
    }

    public void get(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    public void getUsingToken(String url, String action, String header) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header(action, header)
                .url(url)
                .build();

        call = client.newCall(request);
        executeCall();
    }

    private void executeCall() {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                delegate.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                delegate.onResponse(response);
            }
        });
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
