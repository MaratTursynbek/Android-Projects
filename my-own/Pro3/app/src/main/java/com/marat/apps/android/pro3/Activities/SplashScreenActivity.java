package com.marat.apps.android.pro3.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.R;

public class SplashScreenActivity extends AppCompatActivity {

    private Intent intent1;
    private static final int SPLASH_TIME_OUT = 1500;

    private String[] data1 = new String[]{"1", "1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "2500", "123", "456"};
    private String[] data2 = new String[]{"2", "0", "Master Keruen", "ТЦ Керуен, ул. Достык 21, 0-этаж", "3000", "123", "456"};
    private String[] data3 = new String[]{"3", "0", "Быстро", "АЗС NOMAD, ул. Керей-Жанибек 63", "2000", "123", "456"};
    private String[] data4 = new String[]{"4", "1", "АВТО Жуу", "Назарбаев Университет, пр. Кабанбай-Батыра 53, 1-этаж парковки", "2700", "123", "456"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        long startTime = System.currentTimeMillis();

        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if ("".equals(username) || "".equals(password) || "".equals(token)) {
            intent1 = new Intent(this, RegisterActivity.class);
        } else {
            CWStationsDatabase db = new CWStationsDatabase(this);
            db.open();
            int num1 = db.deleteAllStations();
            int num2 = db.deleteFavoriteStations();

            db.addAllCarWashingStations(Integer.parseInt(data1[0]), Integer.parseInt(data1[1]), data1[2], data1[3], Integer.parseInt(data1[4]), Integer.parseInt(data1[5]), Integer.parseInt(data1[6]));
            db.addAllCarWashingStations(Integer.parseInt(data2[0]), Integer.parseInt(data2[1]), data2[2], data2[3], Integer.parseInt(data2[4]), Integer.parseInt(data2[5]), Integer.parseInt(data2[6]));
            db.addAllCarWashingStations(Integer.parseInt(data3[0]), Integer.parseInt(data3[1]), data3[2], data3[3], Integer.parseInt(data3[4]), Integer.parseInt(data3[5]), Integer.parseInt(data3[6]));
            db.addAllCarWashingStations(Integer.parseInt(data4[0]), Integer.parseInt(data4[1]), data4[2], data4[3], Integer.parseInt(data4[4]), Integer.parseInt(data4[5]), Integer.parseInt(data4[6]));

            db.addFavoriteCarWashingStations(Integer.parseInt(data1[0]), Integer.parseInt(data1[1]), data1[2], data1[3], Integer.parseInt(data1[4]), Integer.parseInt(data1[5]), Integer.parseInt(data1[6]));
            db.addFavoriteCarWashingStations(Integer.parseInt(data4[0]), Integer.parseInt(data4[1]), data4[2], data4[3], Integer.parseInt(data4[4]), Integer.parseInt(data4[5]), Integer.parseInt(data4[6]));

            db.close();
            intent1 = new Intent(this, MainActivity.class);
            intent1.putExtra("startPage", "Favorites");
        }

        long endTime = System.currentTimeMillis();

        if ((endTime - startTime) >= SPLASH_TIME_OUT) {
            startActivity(intent1);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent1);
                    finish();
                }
            }, (SPLASH_TIME_OUT - (endTime - startTime)));
        }
    }
}
