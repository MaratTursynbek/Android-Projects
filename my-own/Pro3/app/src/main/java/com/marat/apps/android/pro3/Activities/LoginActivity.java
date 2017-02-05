package com.marat.apps.android.pro3.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Models.PhoneNumberEditText;
import com.marat.apps.android.pro3.Models.PhoneTextWatcher;
import com.marat.apps.android.pro3.Internet.PostRequestResponse;
import com.marat.apps.android.pro3.Internet.UniversalPostRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements PostRequestResponse {

    private String userAuthorizationURL = "https://whispering-crag-11991.herokuapp.com/api/v1/sessions";
    private String formattedPhoneNumber;

    private PhoneNumberEditText phoneNumberEditText;
    private EditText passwordEditText;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumberEditText = (PhoneNumberEditText) findViewById(R.id.LogInPhoneNumberEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        findViewById(R.id.loginLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return true;
            }
        });

        phoneNumberEditText.setHint("(XXX) XXX-XX-XX");
        phoneNumberEditText.addTextChangedListener(new PhoneTextWatcher(phoneNumberEditText));
        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    phoneNumberEditText.clearFocus();
                    passwordEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logInUser(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void logInUser(View v) {
        UniversalPostRequest postRequest = new UniversalPostRequest(this);
        postRequest.delegate = this;
        if (postRequest.isNetworkAvailable()) {
            showProgressDialog();
            postRequest.post(userAuthorizationURL, createUserDataInJson());
        } else {
            hideKeyboard();
            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_SHORT).show();
        }
    }

    private String createUserDataInJson() {
        String phone = phoneNumberEditText.getText().toString();
        formattedPhoneNumber = phone.substring(1, 4) + phone.substring(6, 9) + phone.substring(10, 12) + phone.substring(13);
        return "{\"user\":{"
                + "\"phone_number\":" + "\"" + formattedPhoneNumber + "\"" + ","
                + "\"password\":" + "\"" + passwordEditText.getText().toString() + "\""
                + "}}";
    }

    @Override
    public void onFailure(IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "Could not load data", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onResponse(Response response) {
        String res;
        boolean isSuccessful = false;

        dialog.dismiss();

        try {
            res = response.body().string();

            Log.v("tag", res);

            JSONObject user = new JSONObject(res);
            String token = user.getString("token");
            saveUserData(token);
            isSuccessful = true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if (isSuccessful) {
            Intent finishIntent = new Intent("finish__register_activity");
            LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(finishIntent);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("startPage", "Favorites");
            startActivity(intent);
            finish();
        }
    }

    public void saveUserData(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", formattedPhoneNumber);
        editor.putString("password", passwordEditText.getText().toString());
        editor.putString("ACCESS_TOKEN", token);
        editor.apply();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        phoneNumberEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    public void showProgressDialog() {
        hideKeyboard();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Вход");
        dialog.show();
    }

    public void goToRestorePasswordActivity(View v) {
        Intent intent2 = new Intent(this, RestorePasswordActivity.class);
        startActivity(intent2);
        finish();
    }
}
