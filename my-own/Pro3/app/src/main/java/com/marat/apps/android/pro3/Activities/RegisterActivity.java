package com.marat.apps.android.pro3.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Models.PhoneNumberEditText;
import com.marat.apps.android.pro3.Models.PhoneTextWatcher;
import com.marat.apps.android.pro3.R;

public class RegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener, TextView.OnEditorActionListener, View.OnTouchListener{

    private View registerActivityLayout;
    private PhoneNumberEditText phoneNumberEditText;

    private BroadcastReceiver finishActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("finish_register_activity".equals(intent.getAction())) {
                LocalBroadcastManager.getInstance(RegisterActivity.this).unregisterReceiver(finishActivityReceiver);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LocalBroadcastManager.getInstance(this).registerReceiver(finishActivityReceiver, new IntentFilter("finish_register_activity"));

        registerActivityLayout = findViewById(R.id.registerActivityLayout);
        phoneNumberEditText = (PhoneNumberEditText) findViewById(R.id.registerPhoneNumberEditText);

        phoneNumberEditText.addTextChangedListener(new PhoneTextWatcher(phoneNumberEditText));
        phoneNumberEditText.setOnFocusChangeListener(this);
        phoneNumberEditText.setOnEditorActionListener(this);
        registerActivityLayout.setOnTouchListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.registerPhoneNumberEditText) {
            if (hasFocus) {
                if (phoneNumberEditText.length() == 0) {
                    phoneNumberEditText.setHint(R.string.hint_phone_number);
                }
            } else {
                if (phoneNumberEditText.length() == 0) {
                    phoneNumberEditText.setHint(R.string.hint_new_phone_number);
                }
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.registerPhoneNumberEditText) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                goToCreateAccountActivity(v);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideKeyboard();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideKeyboard();
    }

    public void goToLogInActivity(View v) {
        hideKeyboard();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToCreateAccountActivity(View v) {
        if (phoneNumberIsFullyEntered()) {
            //hideKeyboard();
            Intent intent = new Intent(this, CreateAccountActivity.class);
            intent.putExtra("phone_number", phoneNumberEditText.getText().toString());
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.error_phone_number_format), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        phoneNumberEditText.clearFocus();
    }

    private boolean phoneNumberIsFullyEntered() {
        return (phoneNumberEditText.length() == 15);
    }
}
