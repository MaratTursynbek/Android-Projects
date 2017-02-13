package com.marat.apps.android.pro3.Interfaces;

import java.io.IOException;

import okhttp3.Response;

public interface PostRequestResponse {
    void onFailure(IOException e);
    void onResponse(Response res);
}
