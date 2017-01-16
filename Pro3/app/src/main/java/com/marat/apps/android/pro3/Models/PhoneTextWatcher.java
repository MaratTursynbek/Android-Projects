package com.marat.apps.android.pro3.Models;

import android.text.Editable;
import android.text.TextWatcher;

public class PhoneTextWatcher implements TextWatcher {

    private PhoneEditText phoneNumberEditText;
    private String phoneNumber = "";

    public PhoneTextWatcher(PhoneEditText editText) {
        phoneNumberEditText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable s) {
        String newValue = s.toString();

        if (newValue.length() > phoneNumber.length()) {
            phoneNumber = s.toString();

            if (phoneNumber.length() == 4) {
                if ('(' != phoneNumber.charAt(0)) {
                    phoneNumberEditText.setText("(" + phoneNumber.substring(0, phoneNumber.length() - 1) + ") " + phoneNumber.substring(phoneNumber.length() - 1));
                    phoneNumberEditText.setSelection(phoneNumber.length());
                }
            } else if (phoneNumber.length() == 10) {
                phoneNumberEditText.setText(phoneNumber.substring(0, phoneNumber.length() - 1) + "-" + phoneNumber.substring(phoneNumber.length() - 1));
                phoneNumberEditText.setSelection(phoneNumber.length());
            } else if (phoneNumber.length() == 13) {
                phoneNumberEditText.setText(phoneNumber.substring(0, phoneNumber.length() - 1) + "-" + phoneNumber.substring(phoneNumber.length() - 1));
                phoneNumberEditText.setSelection(phoneNumber.length());
            }
        }
        else if (newValue.length() < phoneNumber.length()) {
            phoneNumber = s.toString();

            if (phoneNumber.length() == 13) {
                phoneNumberEditText.setText(phoneNumber.substring(0, phoneNumber.length() - 1));
                phoneNumberEditText.setSelection(phoneNumber.length());
            }
            else if (phoneNumber.length() == 10) {
                phoneNumberEditText.setText(phoneNumber.substring(0, phoneNumber.length() - 1));
                phoneNumberEditText.setSelection(phoneNumber.length());
            }
            else if (phoneNumber.length() == 6) {
                phoneNumberEditText.setText(phoneNumber.substring(1, phoneNumber.length() - 2));
                phoneNumberEditText.setSelection(phoneNumber.length());
            }
        }
    }
}
