package com.marat.apps.android.pro3.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.marat.apps.android.pro3.Models.PhoneNumberEditText;
import com.marat.apps.android.pro3.Interfaces.PostRequestResponse;
import com.marat.apps.android.pro3.Internet.UniversalPostRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class CreateAccountActivity extends AppCompatActivity implements PostRequestResponse{

    String userRegistrationURL = "https://whispering-crag-11991.herokuapp.com/api/v1/users";
    private String formattedPhoneNumber;

    private EditText userNameEditText, passwordEditText, confirmPasswordEditText, cityEditText, carTypeEditText;
    private PhoneNumberEditText phoneNumberEditText;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        phoneNumberEditText = (PhoneNumberEditText) findViewById(R.id.newPhoneNumberEditText);
        passwordEditText = (EditText) findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText2);
        cityEditText = (EditText) findViewById(R.id.userCityEditText);
        carTypeEditText = (EditText) findViewById(R.id.userCarEditText);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        phoneNumberEditText.setText(extras.getString("phone_number"));
        phoneNumberEditText.setFocusable(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void registerUser(View v) {
        UniversalPostRequest postRequest = new UniversalPostRequest(this);
        postRequest.delegate = this;
        if (postRequest.isNetworkAvailable()) {
            if (userNameIsEntered()) {
                if (passwordIsEntered()) {
                    if (passwordIsConfirmed()) {
                        if (cityIsChosen()) {
                            if (carTypeIsChosen()) {
                                showProgressDialog();
                                postRequest.post(userRegistrationURL, createNewUserDataInJson());
                            } else {
                                showSnackErrorMessage("Выберите Ваш тип машины", v);
                            }
                        } else {
                            showSnackErrorMessage("Выберите Ваш город", v);
                        }
                    } else {
                        showSnackErrorMessage("Введенные пароли не совпадают", v);
                    }
                } else {
                    showSnackErrorMessage("Выберите пароль", v);
                }
            } else {
                showSnackErrorMessage("Выберите Ваше Имя", v);
            }
        } else {
            hideKeyboard();
            showSnackErrorMessage("Нет доступа к Интернету", v);
        }
    }

    private String createNewUserDataInJson() {
        String phone = phoneNumberEditText.getText().toString();
        formattedPhoneNumber = phone.substring(1,4) + phone.substring(6,9) + phone.substring(10,12) + phone.substring(13);

        return "{\"user\":{"
                +   "\"phone_number\":"            +   "\""   +   formattedPhoneNumber                                 +    "\""   +   ","
                +   "\"name\":"                    +   "\""   +   userNameEditText.getText().toString()          +    "\""   +   ","
                +   "\"password\":"                +   "\""   +   passwordEditText.getText().toString()          +    "\""   +   ","
                +   "\"password_confirmation\":"   +   "\""   +   confirmPasswordEditText.getText().toString()   +    "\""   +   ","
                +   "\"city_id\":"                    +   "\""   +   cityEditText.getText().toString()              +    "\""   +   ","
                +   "\"car_type_id\":"                +   "\""   +   carTypeEditText.getText().toString()               +    "\""
                +   "}}";
    }

    @Override
    public void onFailure(IOException e) {
        e.printStackTrace();
        showErrorToast("Не удалось загрузить данные");
    }

    @Override
    public void onResponse(Response response) {
        dialog.dismiss();
        boolean isSuccessful = false;
        String responseMessage = response.message();
        Log.d("LoginActivity", responseMessage);

        if ("Created ".equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d("CreateAccountActivity", res);
                JSONObject user = new JSONObject(res);
                String userId = user.getString("id");
                String token = user.getString("token");
                saveUserData(userId, token);
                isSuccessful = true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            if (isSuccessful) {
                Intent finishIntent = new Intent("finish__register_activity");
                LocalBroadcastManager.getInstance(CreateAccountActivity.this).sendBroadcast(finishIntent);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("startPage", "AllCarWashers");
                startActivity(intent);
                finish();
            }
        } else {
            showErrorToast("Не удалось создать пользователя");
        }
    }

    private boolean userNameIsEntered() {
        return (userNameEditText.length() >= 3);
    }

    private boolean passwordIsEntered() {
        return (passwordEditText.length() >= 6);
    }

    private boolean passwordIsConfirmed() {
        return (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString()));
    }

    private boolean cityIsChosen() {
        return (cityEditText.length() != 0);
    }

    private boolean carTypeIsChosen() {
        return (carTypeEditText.length() != 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        phoneNumberEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    private void showProgressDialog() {
        hideKeyboard();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Регистрация");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showSnackErrorMessage(String message, View v) {
        Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(CreateAccountActivity.this, android.R.color.holo_red_dark));
        snackbar.show();
    }

    private void showErrorToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveUserData(String userId, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", formattedPhoneNumber);
        editor.putString("password", passwordEditText.getText().toString());
        editor.putString("user_id", userId);
        editor.putString("ACCESS_TOKEN", token);
        editor.apply();
    }
}
