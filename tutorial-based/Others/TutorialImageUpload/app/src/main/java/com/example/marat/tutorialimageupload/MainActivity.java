package com.example.marat.tutorialimageupload;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    public static final String TAG = "myLog";

    TextView dateText;
    ImageView imageView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateText = (TextView) findViewById(R.id.dateTextView);
        imageView = (ImageView) findViewById(R.id.bgImage);
        btn = (Button) findViewById(R.id.changeButton);

        long c = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        String dateString  = format.format(c);
        dateText.setText(dateString );

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick");
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "beforeIf");
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Log.v(TAG, "inSide if");

            Uri selectedImage = data.getData();
            Log.v(TAG, "1");
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Log.v(TAG, "2");
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            Log.v(TAG, "3");
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            Log.v(TAG, "4");
            String picturePath = cursor.getString(columnIndex);
            Log.v(TAG, "5");
            cursor.close();
            Log.v(TAG, "6");

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Log.v(TAG, "image is set");
        }
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
}
