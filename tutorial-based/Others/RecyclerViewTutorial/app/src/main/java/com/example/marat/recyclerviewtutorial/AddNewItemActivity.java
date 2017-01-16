package com.example.marat.recyclerviewtutorial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddNewItemActivity extends AppCompatActivity {

    private static final String TAG = "myTag";

    EditText editText, seconds;
    Button button;
    int order, sec;

    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.newText);
        seconds = (EditText) findViewById(R.id.waitTimeEditText);
        button = (Button) findViewById(R.id.saveNewItem);

        Intent i = getIntent();
        Bundle args = i.getExtras();
        String max = args.getString("max");

        Toast.makeText(this, max, Toast.LENGTH_SHORT).show();

        db = new Database(AddNewItemActivity.this);
        db.open();

        order = Integer.parseInt(max);
        order++;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                sec = Integer.parseInt(seconds.getText().toString());
                db.addData(name, order, sec);
                registerNotification();
                finish();
            }
        });
    }

    private void registerNotification() {

        Log.v(TAG, "registerNotification()");

        ArrayList<String> newItem = new ArrayList<String>();
        newItem = db.getLastRowId();

        Intent notif = new Intent(this, AlarmBroadcastReceiver.class);
        notif.putExtra("Text", newItem.get(1));
        notif.putExtra("Position", newItem.get(0));

        Log.v(TAG, "before Pending Intent");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(newItem.get(0)), notif, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (sec * 1000), pendingIntent);

        Log.v(TAG, "Alarm is set");
    }
}