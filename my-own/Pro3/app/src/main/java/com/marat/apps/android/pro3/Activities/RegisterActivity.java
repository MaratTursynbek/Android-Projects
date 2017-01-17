package com.marat.apps.android.pro3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

        findViewById(R.id.registerAccount).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return true;
            }
        });

        phoneNumberEditText = (PhoneNumberEditText) findViewById(R.id.registerPhoneNumberEditText);
        phoneNumberEditText.setHint("(XXX) XXX-XX-XX");
        PhoneTextWatcher phoneTextWatcher = new PhoneTextWatcher(phoneNumberEditText);
        phoneNumberEditText.addTextChangedListener(phoneTextWatcher);
    }

    public void goToLogInActivity(View v) {
        Intent intent1 = new Intent(this, LoginActivity.class);
        startActivity(intent1);
    }

    public void goToCreateAccountActivity(View v) {
        hideKeyboard();
        Intent intent1 = new Intent(this, CreateAccountActivity.class);
        startActivity(intent1);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        phoneNumberEditText.clearFocus();
    }

}
