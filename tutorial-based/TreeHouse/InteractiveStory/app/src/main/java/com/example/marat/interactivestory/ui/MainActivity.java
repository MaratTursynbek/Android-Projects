package com.example.marat.interactivestory.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.marat.interactivestory.R;


public class MainActivity extends Activity {

    private EditText mNameField;
    private Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameField = (EditText) findViewById(R.id.nameEditText);
        mStartButton = (Button) findViewById(R.id.startButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameField.getText().toString();
                startActivity(name);
            }
        });

    }

    private void startActivity(String name) {
        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNameField.setText("");
    }
}
