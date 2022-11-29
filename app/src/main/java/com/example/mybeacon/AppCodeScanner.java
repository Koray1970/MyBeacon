package com.example.mybeacon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class AppCodeScanner extends AppCompatActivity {
    MaterialButton materialButton;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.codescanner);
        materialButton=(MaterialButton) findViewById(R.id.scanbtn);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
                Log.v("test","test1");
            }
        });
    }

    private void scanCode() {
        ScanOptions scanOptions=new ScanOptions();
        scanOptions.setPrompt("Volumn up to flash on");
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(CapureAct.class);
        barcodeLauncher.launch(scanOptions);
    }


    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(AppCodeScanner.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AppCodeScanner.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });
}
