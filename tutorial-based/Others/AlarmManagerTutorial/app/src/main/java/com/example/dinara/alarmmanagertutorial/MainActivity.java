package com.example.dinara.alarmmanagertutorial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button mStart1, mStart2, mStop;
    EditText mSeconds;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStart1 = (Button) findViewById(R.id.setAlarm);
        mStart2 = (Button) findViewById(R.id.setRepeating);
        mStop = (Button) findViewById(R.id.stop);
        mSeconds = (EditText) findViewById(R.id.editText);

        mStart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int i = Integer.parseInt(mSeconds.getText().toString());
                    Intent intent = new Intent(MainActivity.this, AlarmReceiverActivity.class);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(MainActivity.this, 2, intent,
                                    PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);

                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplicationContext(),
                            "Alarm for Activity is set in: " + i + " seconds", Toast.LENGTH_SHORT);
                    mToast.show();

                } catch (NumberFormatException e) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplicationContext(),
                            "Please enter some number and try again!", Toast.LENGTH_SHORT);
                    mToast.show();
                    Log.i("MainActivity", "Number format exception");
                }
            }
        });

        mStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int i = Integer.parseInt(mSeconds.getText().toString());
                    Intent intent = new Intent(MainActivity.this, RepeatingAlarmReceiverActivity.class);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(MainActivity.this, 3, intent,
                                    PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), 15 * 1000, pendingIntent);

                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplicationContext(),
                            "Repeating Alarm for Activity is set in: " + i + " seconds,"
                                    + " and repeat every 15 seconds", Toast.LENGTH_SHORT);
                    mToast.show();

                } catch (NumberFormatException e) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getApplicationContext(),
                            "Please enter some number and try again!", Toast.LENGTH_SHORT);
                    mToast.show();
                    Log.i("MainActivity", "Number format exception");
                }
            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RepeatingAlarmReceiverActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(MainActivity.this, 3, intent, 0);
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.cancel(pendingIntent);

                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(getApplicationContext(),
                        "Repeating Alarm has been cancelled!", Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

    }
}
