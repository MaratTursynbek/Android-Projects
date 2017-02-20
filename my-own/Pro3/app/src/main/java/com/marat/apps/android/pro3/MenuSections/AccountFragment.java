package com.marat.apps.android.pro3.MenuSections;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marat.apps.android.pro3.Interfaces.OnToolbarTitleChangeListener;
import com.marat.apps.android.pro3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountFragment extends Fragment {

    private static final String TAG = "myTag";

    private String userURL = "https://whispering-crag-11991.herokuapp.com/api/v1/users/4";
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        OnToolbarTitleChangeListener listener = (OnToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_account));

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("ACCESS_TOKEN", "");
        getUser();

        return v;
    }

    public void getUser() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "Token token=\"" + token + "\"")
                .url(userURL)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Exception caught: ", e);
                alertUserAboutError(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String userInfo = null;

                try {
                    userInfo = response.body().string();
                    Log.v(TAG, "User Info: " + userInfo);
                    parseJSON(userInfo);
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private void parseJSON(String input) throws JSONException {
        JSONObject data = new JSONObject(input);
        JSONObject user = data.getJSONObject("user");
    }

    private void alertUserAboutError(int id) {
        if (id == 1) {
            Toast.makeText(getContext(), "Unable to load data", Toast.LENGTH_SHORT).show();
        } else if (id == 2) {
            Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
        }
    }
}
