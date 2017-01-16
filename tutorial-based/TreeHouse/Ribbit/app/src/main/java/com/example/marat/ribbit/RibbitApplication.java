package com.example.marat.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Marat on 18/08/2015.
 */
public class RibbitApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "uDhSNobgmvvGNSRq4V70jeOFwlROrFm3gGzcPyV6", "0qbXwL203iEZ9R1NXuZ87CNbU7WYGWWSRKtDDw94");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
