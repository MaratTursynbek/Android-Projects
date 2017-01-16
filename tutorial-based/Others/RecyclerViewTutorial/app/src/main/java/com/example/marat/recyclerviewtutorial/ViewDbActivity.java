package com.example.marat.recyclerviewtutorial;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewDbActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_db);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView = (TextView) findViewById(R.id.table);

        Database db = new Database(ViewDbActivity.this);
        db.open();
        ArrayList<ArrayList<String>> data = db.getData();
        String result = "";

        for (ArrayList<String> aData : data) {
            for (int j = 0; j < 4; j++) {
                result = result + aData.get(j) + "       ";
            }
            result = result + "\n\n";
        }

        textView.setText(result);
    }

    public void showToast(View v) {
        Toast.makeText(this, "Button is clicked", Toast.LENGTH_SHORT).show();
    }
}