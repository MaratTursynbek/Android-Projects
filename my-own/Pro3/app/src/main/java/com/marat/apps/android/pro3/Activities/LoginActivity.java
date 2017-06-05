package com.marat.apps.android.pro3.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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

import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Databases.StoreToDatabaseHelper;
import com.marat.apps.android.pro3.Models.PhoneNumberEditText;
import com.marat.apps.android.pro3.Models.PhoneTextWatcher;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.PostRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements RequestResponseListener, TextView.OnEditorActionListener, View.OnTouchListener {

    private static final String TAG = "logtag";

    private static final String USER_AUTHORIZATION_URL = "https://propropro.herokuapp.com/api/v1/sessions";
    private String formattedPhoneNumber;

    private View loginActivityLayout;
    private PhoneNumberEditText phoneNumberEditText;
    private EditText passwordEditText;
    private ProgressDialog dialog;

    private PostRequest postRequest;

    private boolean isSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginActivityLayout = findViewById(R.id.logInActivityLayout);
        phoneNumberEditText = (PhoneNumberEditText) findViewById(R.id.logInPhoneNumberEditText);
        passwordEditText = (EditText) findViewById(R.id.logInPasswordEditText);

        phoneNumberEditText.addTextChangedListener(new PhoneTextWatcher(phoneNumberEditText));
        phoneNumberEditText.setOnEditorActionListener(this);
        passwordEditText.setOnEditorActionListener(this);
        loginActivityLayout.setOnTouchListener(this);

        postRequest = new PostRequest(this);
        postRequest.delegate = this;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean isHandled = false;
        switch (v.getId()) {
            case R.id.logInPhoneNumberEditText:
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    phoneNumberEditText.clearFocus();
                    passwordEditText.requestFocus();
                    isHandled = true;
                }
                break;
            case R.id.logInPasswordEditText:
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logInUser(v);
                    isHandled = true;
                }
                break;
        }
        return isHandled;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideKeyboard();
        return true;
    }

    public void logInUser(View v) {
        postRequest = new PostRequest(this);
        postRequest.delegate = this;

        if (postRequest.isNetworkAvailable()) {
            if (phoneNumberIsFullyEntered()) {
                if (passwordIsEntered()) {
                    showProgressDialog();
                    postRequest.post(USER_AUTHORIZATION_URL, createUserDataInJson());
                } else {
                    showSnackErrorMessage(getString(R.string.error_enter_password), v);
                }
            } else {
                showSnackErrorMessage(getString(R.string.error_phone_number_format), v);
            }
        } else {
            hideKeyboard();
            showErrorToast(getString(R.string.error_no_internet_connection));
        }
    }

    private String createUserDataInJson() {
        String phone = phoneNumberEditText.getText().toString();
        formattedPhoneNumber = phone.substring(1, 4) + phone.substring(6, 9) + phone.substring(10, 12) + phone.substring(13);
        return "{\"user\":{" +

                "\"phone_number\":" + "\"" + formattedPhoneNumber + "\"" + "," +
                "\"password\":" + "\"" + passwordEditText.getText().toString() + "\"" +

                "}}";
    }

    @Override
    public void onFailure(IOException e) {
        e.printStackTrace();
        dialog.dismiss();
        showErrorToast(getString(R.string.error_could_not_load_data));
    }

    @Override
    public void onResponse(Response response) {
        dialog.dismiss();
        String responseMessage = response.message();
        Log.d(TAG, "LoginActivity: " + responseMessage);

        if (getString(R.string.server_response_created).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "LoginActivity: " + res);
                JSONObject responseJSON = new JSONObject(res);
                JSONObject userObject = responseJSON.getJSONObject("user");
                saveUserData(userObject);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            if (isSuccessful) {
                Intent finishIntent = new Intent("finish_register_activity");
                LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(finishIntent);
                Intent intent = new Intent(this, MainActivity.class);
                CarWashesDatabase db = new CarWashesDatabase(this);
                db.open();
                if (db.userHasFavoriteStations()) {
                    intent.putExtra("start_page", "favorite_car_washes");
                } else {
                    intent.putExtra("start_page", "all_car_washes");
                }
                startActivity(intent);
                finish();
            }
        } else if (getString(R.string.server_response_unauthorized).equals(responseMessage)) {
            showErrorToast(getString(R.string.error_wrong_phone_or_pass));
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
    }

    private boolean phoneNumberIsFullyEntered() {
        return (phoneNumberEditText.length() == 15);
    }

    private boolean passwordIsEntered() {
        return (passwordEditText.length() > 0);
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
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void saveUserData(JSONObject userObject) throws JSONException {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(this);
        isSuccessful = helper.saveUserLogInData(userObject, passwordEditText.getText().toString());
    }

    private void showSnackErrorMessage(String message, View v) {
        Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(LoginActivity.this, android.R.color.holo_red_dark));
        snackbar.show();
    }

    private void showErrorToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToRestorePasswordActivity(View v) {
        Intent intent2 = new Intent(this, RestorePasswordActivity.class);
        startActivity(intent2);
        finish();
    }

    @Override
    protected void onPause() {
        postRequest.cancelCall();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onPause();
    }
}
