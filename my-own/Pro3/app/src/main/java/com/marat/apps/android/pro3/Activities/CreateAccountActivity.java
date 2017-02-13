package com.marat.apps.android.pro3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.marat.apps.android.pro3.Models.PhoneNumberEditText;
import com.marat.apps.android.pro3.Interfaces.PostRequestResponse;
import com.marat.apps.android.pro3.Internet.UniversalPostRequest;
import com.marat.apps.android.pro3.R;

import java.io.IOException;

import okhttp3.Response;

public class CreateAccountActivity extends AppCompatActivity implements PostRequestResponse{

    String userRegistrationURL = "https://whispering-crag-11991.herokuapp.com/api/v1/users";

    private EditText userNameEditText, passwordEditText, confirmPasswordEditText, cityEditText, carTypeEditText;
    private PhoneNumberEditText phoneNumberEditText;

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
        postRequest.post(userRegistrationURL, createNewUserDataInJson());
    }

    private String createNewUserDataInJson() {
        String phone = phoneNumberEditText.getText().toString();
        String formattedPhone = phone.substring(1,4) + phone.substring(6,9) + phone.substring(10,12) + phone.substring(13);

        return "{\"user\":{"
                +   "\"phone_number\":"            +   "\""   +   formattedPhone                                 +    "\""   +   ","
                +   "\"name\":"                    +   "\""   +   userNameEditText.getText().toString()          +    "\""   +   ","
                +   "\"password\":"                +   "\""   +   passwordEditText.getText().toString()          +    "\""   +   ","
                +   "\"password_confirmation\":"   +   "\""   +   confirmPasswordEditText.getText().toString()   +    "\""   +   ","
                +   "\"city\":"                    +   "\""   +   cityEditText.getText().toString()              +    "\""   +   ","
                +   "\"car_type\":"                +   "\""   +   carTypeEditText.getText().toString()               +    "\""
                +   "}}";
    }

    @Override
    public void onFailure(IOException e) {
        e.printStackTrace();
        Toast.makeText(this, "Could not load data", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Response res) {
        try {
            Log.v("myTag", res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
