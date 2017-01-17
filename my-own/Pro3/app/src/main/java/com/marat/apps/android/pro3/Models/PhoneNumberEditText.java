package com.marat.apps.android.pro3.Models;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class PhoneNumberEditText extends EditText {

    public PhoneNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {

        CharSequence text = getText();
        if (text != null) {
            if (text.length() != selStart || text.length() != selEnd) {
                setSelection(text.length(), text.length());
                return;
            }
        }

        super.onSelectionChanged(selStart, selEnd);
    }
}
