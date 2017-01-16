package com.marat.apps.android.pro3.Internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UniversalPostRequest {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Context context;
    public PostRequestResponse delegate = null;

    public UniversalPostRequest(Context c) {
        context = c;
    }

    public void post(String url, String json) {

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Call call = client.newCall(request);
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
        else {
            Toast.makeText(context, "Network is unavailable!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }
}
