package com.marat.apps.android.pro3.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Models.PhoneNumberEditText;
import com.marat.apps.android.pro3.Models.PhoneTextWatcher;
import com.marat.apps.android.pro3.R;

public class RegisterActivity extends AppCompatActivity {

    private PhoneNumberEditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LocalBroadcastManager.getInstance(this).registerReceiver(finishActivityReceiver, new IntentFilter("finish__register_activity"));

        findViewById(R.id.registerAccount).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return true;
            }
        });

        phoneNumberEditText = (PhoneNumberEditText) findViewById(R.id.registerPhoneNumberEditText);
        phoneNumberEditText.addTextChangedListener(new PhoneTextWatcher(phoneNumberEditText));
        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
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
        });
        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Log.v("tag", "action triggered");
                    goToCreateAccountActivity(v);
                    return true;
                }
                return false;
            }
        });
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
            Toast.makeText(this, "Incorrect Phone Number", Toast.LENGTH_SHORT).show();
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

    private BroadcastReceiver finishActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("finish__register_activity".equals(intent.getAction())) {
                LocalBroadcastManager.getInstance(RegisterActivity.this).unregisterReceiver(finishActivityReceiver);
                finish();
            }
        }
    };
}
