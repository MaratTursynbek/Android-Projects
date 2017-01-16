package com.example.marat.days.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.marat.days.DB.DaysDatabase;
import com.example.marat.days.R;

public class ViewDatabaseActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_database);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv = (TextView) findViewById(R.id.output);

        DaysDatabase db = new DaysDatabase(ViewDatabaseActivity.this);
        db.open();
        String data[][] = db.getData();
        String result = "";

        for (String[] aData : data) {
            for (int j = 0; j < 5; j++) {
                result = result + aData[j] + " ";
            }
            result = result + "\n\n";
        }

        tv.setText(result);
    }
}