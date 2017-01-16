package com.example.marat.recyclerviewtutorial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ItemInformationActivity extends AppCompatActivity {

    TextView textView;
    String info, itemPosition;
    Button deleteAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView = (TextView) findViewById(R.id.infoText);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        deleteAlarm = (Button) findViewById(R.id.deleteAlarm);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        info = args.getString("Text");
        itemPosition = args.getString("Position");
        textView.setText(info);

        deleteAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notif = new Intent(ItemInformationActivity.this, AlarmBroadcastReceiver.class);
                notif.putExtra("Text", info);
                notif.putExtra("Position", itemPosition);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(ItemInformationActivity.this, Integer.parseInt(itemPosition), notif, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.cancel(pendingIntent);
            }
        });

        Toast.makeText(this, itemPosition, Toast.LENGTH_SHORT).show();

        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db = new Database(ItemInformationActivity.this);
                db.open();
                db.deleteData(itemPosition);
                db.close();
                finish();
            }
        });
    }
}
