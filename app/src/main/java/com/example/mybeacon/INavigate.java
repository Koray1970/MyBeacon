package com.example.mybeacon;

import android.content.Intent;

public class INavigate {
    public INavigate(String id){
        if (id == R.id.addbeacon) {
            Intent i =new Intent(MainActivity.this, AppCodeScanner.class);
            startActivity(i);
            return true;
        }
    }
}
