package com.marat.apps.android.pro3.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.R;

public class SplashScreenActivity extends AppCompatActivity {

    private Intent intent1;
    private static final int SPLASH_TIME_OUT = 1500;

    private String[] data1 = new String[]{"1", "1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "2500", "123", "456"};
    private String[] data2 = new String[]{"2", "0", "Master Keruen", "ТЦ Керуен, ул. Достык 21, 0-этаж", "3000", "123", "456"};
    private String[] data3 = new String[]{"3", "0", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "2000", "123", "456"};
    private String[] data4 = new String[]{"4", "1", "АВТО Жуу", "Назарбаев Университет, пр. Кабанбай-Батыра 53, 1-этаж парковки", "2700", "123", "456"};

    private String[] order1 = new String[]{"1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "Кузов + Салон + Багажник", "02.02.2017", "3500", "Активный"};
    private String[] order2 = new String[]{"3", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "Кузов + Салон", "28.01.2017", "2800", "Завершен"};
    private String[] order3 = new String[]{"3", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "Кузов + Салон", "25.01.2017", "2800", "Завершен"};
    private String[] order4 = new String[]{"1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "Кузов + Салон + Багажник + Двигатель", "20.01.2017", "4500", "Завершен"};
    private String[] order5 = new String[]{"1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "Кузов + Салон", "15.01.2017", "2500", "Завершен"};
    private String[] order6 = new String[]{"2", "Master Keruen", "ТЦ Керуен, ул. Достык 21, 0-этаж", "Кузов + Салон", "13.01.2017", "2700", "Завершен"};
    private String[] order7 = new String[]{"3", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "Кузов + Салон", "09.01.2017", "2800", "Завершен"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        long startTime = System.currentTimeMillis();
        initializeDatabase();

        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if ("".equals(username) || "".equals(password) || "".equals(token)) {
            intent1 = new Intent(this, RegisterActivity.class);
        } else {
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

    private void initializeDatabase() {
        CWStationsDatabase db = new CWStationsDatabase(this);
        db.open();
        db.deleteAllStations();
        db.deleteFavoriteStations();
        db.deleteMyOrders();

        db.addAllCarWashingStations(Integer.parseInt(data1[0]), Integer.parseInt(data1[1]), data1[2], data1[3], Integer.parseInt(data1[4]), Integer.parseInt(data1[5]), Integer.parseInt(data1[6]));
        db.addAllCarWashingStations(Integer.parseInt(data2[0]), Integer.parseInt(data2[1]), data2[2], data2[3], Integer.parseInt(data2[4]), Integer.parseInt(data2[5]), Integer.parseInt(data2[6]));
        db.addAllCarWashingStations(Integer.parseInt(data3[0]), Integer.parseInt(data3[1]), data3[2], data3[3], Integer.parseInt(data3[4]), Integer.parseInt(data3[5]), Integer.parseInt(data3[6]));
        db.addAllCarWashingStations(Integer.parseInt(data4[0]), Integer.parseInt(data4[1]), data4[2], data4[3], Integer.parseInt(data4[4]), Integer.parseInt(data4[5]), Integer.parseInt(data4[6]));

        db.addFavoriteCarWashingStations(Integer.parseInt(data1[0]), Integer.parseInt(data1[1]), data1[2], data1[3], Integer.parseInt(data1[4]), Integer.parseInt(data1[5]), Integer.parseInt(data1[6]));
        db.addFavoriteCarWashingStations(Integer.parseInt(data4[0]), Integer.parseInt(data4[1]), data4[2], data4[3], Integer.parseInt(data4[4]), Integer.parseInt(data4[5]), Integer.parseInt(data4[6]));

        long p = db.addMyOrders(Integer.parseInt(order1[0]), order1[1], order1[2], order1[3], order1[4], order1[5], order1[6]);
        Log.v("SplashScreenActivity", p + "");
        db.addMyOrders(Integer.parseInt(order2[0]), order2[1], order2[2], order2[3], order2[4], order2[5], order2[6]);
        db.addMyOrders(Integer.parseInt(order3[0]), order3[1], order3[2], order3[3], order3[4], order3[5], order3[6]);
        db.addMyOrders(Integer.parseInt(order4[0]), order4[1], order4[2], order4[3], order4[4], order4[5], order4[6]);
        db.addMyOrders(Integer.parseInt(order5[0]), order5[1], order5[2], order5[3], order5[4], order5[5], order5[6]);
        db.addMyOrders(Integer.parseInt(order6[0]), order6[1], order6[2], order6[3], order6[4], order6[5], order6[6]);
        db.addMyOrders(Integer.parseInt(order7[0]), order7[1], order7[2], order7[3], order7[4], order7[5], order7[6]);

        db.close();
    }
}
