package com.example.marat.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button mSaveData, mViewList, mGetInfo, mEditRow, mDeleteRow;
    EditText mNameField, mHotnessField, mRowIdField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSaveData = (Button) findViewById(R.id.saveButton);
        mViewList = (Button) findViewById(R.id.viewListButton);
        mGetInfo = (Button) findViewById(R.id.getInfoButton);
        mEditRow = (Button) findViewById(R.id.editRowButton);
        mDeleteRow = (Button) findViewById(R.id.deleteRowButton);

        mNameField = (EditText) findViewById(R.id.nameEditText);
        mHotnessField = (EditText) findViewById(R.id.hotnessEditText);
        mRowIdField = (EditText) findViewById(R.id.rowIdEditText);

        mSaveData.setOnClickListener(this);
        mViewList.setOnClickListener(this);
        mGetInfo.setOnClickListener(this);
        mEditRow.setOnClickListener(this);
        mDeleteRow.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.saveButton:

                boolean didItWork = true;

                try {

                    String name = mNameField.getText().toString();
                    String hotness = mHotnessField.getText().toString();

                    HotOrNot entry = new HotOrNot(this);
                    entry.open();
                    entry.createEntry(name, hotness);
                    entry.close();

                } catch (Exception e) {
                    didItWork = false;
                } finally {
                    if (didItWork) {
                        AlertDialog.Builder d = new AlertDialog.Builder(this);
                        d.setTitle("Heck Yea!");
                        TextView tv = new TextView(this);
                        tv.setText("Success");
                        d.setView(tv);
                        d.show();
                    }
                }

                break;
            case R.id.viewListButton:

                Intent i = new Intent(MainActivity.this, ViewDatabaseActivity.class);
                startActivity(i);

                break;
            case R.id.getInfoButton:

                String s = mRowIdField.getText().toString();
                long l = Long.parseLong(s);

                Toast.makeText(this, l + "", Toast.LENGTH_SHORT).show();

                HotOrNot hon = new HotOrNot(this);
                try {
                    hon.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String returnedName = hon.getName(l);
                String returnedHotness = hon.getHotness(l);

                hon.close();

                mNameField.setText(returnedName);
                mHotnessField.setText(returnedHotness);

                break;
            case R.id.editRowButton:

                String newName = mNameField.getText().toString();
                String newHotness = mHotnessField.getText().toString();

                String sUpdate = mRowIdField.getText().toString();
                long lRow = Long.parseLong(sUpdate);

                HotOrNot update = new HotOrNot(this);
                try {
                    update.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                update.updateEntry(lRow, newName, newHotness);

                update.close();

                break;
            case R.id.deleteRowButton:

                String sDelete = mRowIdField.getText().toString();
                long lDeleteRow = Long.parseLong(sDelete);

                HotOrNot delete = new HotOrNot(this);
                try {
                    delete.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                delete.deleteEntry(lDeleteRow);

                delete.close();

                break;
        }
    }
}
