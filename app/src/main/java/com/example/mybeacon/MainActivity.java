package com.example.mybeacon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybeacon.databinding.ActivityMainBinding;
import com.minew.beaconplus.sdk.MTCentralManager;
import com.minew.beaconplus.sdk.MTPeripheral;
import com.minew.beaconplus.sdk.enums.BluetoothState;
import com.minew.beaconplus.sdk.enums.ConnectionStatus;
import com.minew.beaconplus.sdk.exception.MTException;
import com.minew.beaconplus.sdk.interfaces.ConnectionStatueListener;
import com.minew.beaconplus.sdk.interfaces.GetPasswordListener;
import com.minew.beaconplus.sdk.interfaces.MTCentralManagerListener;
import com.minew.beaconplus.sdk.interfaces.OnBluetoothStateChangedListener;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int PERMISSION_COARSE_LOCATION = 2;
    private static final int REQUEST_FINE_LOCATION = 125;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    MTCentralManager mtCentralManager;
    BluetoothState bluetoothState;

    RecyclerView recyclerView;
    MTMagazinAdapter mAdapter;
    List<MTPeripheral> mtPeripherals = new ArrayList<>();
    public static MTPeripheral mtPeripheral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialButton qrcodescanner=(MaterialButton)findViewById(R.id.scanqrcode);
        qrcodescanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(MainActivity.this, AppCodeScanner.class);
                startActivity(i);
            }
        });
        MaterialButton btnmapopener=(MaterialButton)findViewById(R.id.mappagebtn);
        btnmapopener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(MainActivity.this, DashBoard.class);
                startActivity(i);
            }
        });


        setSupportActionBar(binding.toolbar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        /*NavController navController = Navigation.findNavController(this, R.id.);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/

       /* binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth desteklenmiyor", Toast.LENGTH_LONG).show();
        }
        recyclerView = findViewById(R.id.rvlist);
        MTCentralManager_OnInitView();
        MTCentralManager_OnInit();
        getRequiredPermissions();
        MTCentralManager_OnInitListener();
        mtCentralManager.stopScan();
        try {
            Thread.sleep(2000);
            Intent i =new Intent(MainActivity.this,DashBoard.class);
            startActivity(i);
        }
        catch (Exception e){

        }

    }

    private boolean ensureBleExists() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Phone does not support BLE", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    protected boolean isBLEEnabled() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    private void showBLEDialog() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                initData();
            } else {
                finish();
            }
        }
    }


    private void MTCentralManager_OnInit() {
        mtCentralManager = MTCentralManager.getInstance(this);
        mtCentralManager.startService();
        bluetoothState = mtCentralManager.getBluetoothState(this);
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Log.e("tag", "Bluetooth desteklenmiyor!");
                break;
            case BluetoothStatePowerOff:
                Log.e("tag", "Bluetooth kapal??!");
                break;
            case BluetoothStatePowerOn:
                Log.e("tag", "Bluetooth a????k.");
                break;
        }
        mtCentralManager.setBluetoothChangedListener(new OnBluetoothStateChangedListener() {
            @Override
            public void onStateChanged(BluetoothState bluetoothState) {
                switch (bluetoothState) {
                    case BluetoothStateNotSupported:
                        Log.e("tag", "BluetoothStateNotSupported");
                        break;
                    case BluetoothStatePowerOff:
                        Log.e("tag", "BluetoothStatePowerOff");
                        break;
                    case BluetoothStatePowerOn:
                        Log.e("tag", "BluetoothStatePowerOn");
                        break;
                }
            }
        });
    }

    private void getRequiredPermissions() {
        String[] requestPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions = new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

        } else {
            requestPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

        }
        ActivityCompat.requestPermissions(this,
                requestPermissions, REQUEST_FINE_LOCATION);
    }

    private void MTCentralManager_OnInitView() {
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MTMagazinAdapter();
        recyclerView.setAdapter(mAdapter);
    }


    private void MTCentralManager_OnInitListener() {
        mtCentralManager.setMTCentralManagerListener(new MTCentralManagerListener() {
            @Override
            public void onScanedPeripheral(final List<MTPeripheral> peripherals) {
                Log.v("MCA :", " " + peripherals.size());

                mAdapter.setData(peripherals);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(2000);
                alphaAnimation.setFillAfter(true);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                progressBar.startAnimation(alphaAnimation);

            }
        });

        mAdapter.setOnItemClickListener(new MTMagazinAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                mtPeripheral = mAdapter.getData(position);
                Log.e("tagOnItemClick", "PositionQ : " + position);

                /*AlertDialog.Builder abuilder = new AlertDialog.Builder(MainActivity.this);
                abuilder.setMessage(Integer.toString(position));
                abuilder.setCancelable(false);
                AlertDialog alertDialog = abuilder.create();
                alertDialog.show();*/

                mtCentralManager.connect(mtPeripheral, connectionStatueListener);
            }

            /* @Override
             public void OnItemClickCheck(View view,int position) {
                 String rr = Integer.toString(position);
                 Log.e("tagOnItemClick", "Position : "+rr);
                 Toast.makeText(MainActivity.this, rr, Toast.LENGTH_SHORT).show();

             }*/
            @Override
            public void onLongClick(View view, int position) {

            }
        });
    }

    private ConnectionStatueListener connectionStatueListener = new ConnectionStatueListener() {
        @Override
        public void onUpdateConnectionStatus(final ConnectionStatus connectionStatus, final GetPasswordListener getPasswordListener) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (connectionStatus) {
                        case CONNECTING:
                            Log.e("tag", "CONNECTING");
                            Toast.makeText(MainActivity.this, "CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case CONNECTED:
                            Log.e("tag", "CONNECTED");
                            Toast.makeText(MainActivity.this, "CONNECTED", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGINFO:
                            Log.e("tag", "READINGINFO");
                            Toast.makeText(MainActivity.this, "READINGINFO", Toast.LENGTH_SHORT).show();
                            break;
                        case DEVICEVALIDATING:
                            Log.e("tag", "DEVICEVALIDATING");
                            Toast.makeText(MainActivity.this, "DEVICEVALIDATING", Toast.LENGTH_SHORT).show();
                            break;
                        case PASSWORDVALIDATING:
                            Log.e("tag", "PASSWORDVALIDATING");
                            Toast.makeText(MainActivity.this, "PASSWORDVALIDATING", Toast.LENGTH_SHORT).show();
                            String password = "minew123";
                            getPasswordListener.getPassword(password);
                            break;
                        case SYNCHRONIZINGTIME:
                            Log.e("tag", "SYNCHRONIZINGTIME");
                            Toast.makeText(MainActivity.this, "SYNCHRONIZINGTIME", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGCONNECTABLE:
                            Log.e("tag", "READINGCONNECTABLE");
                            Toast.makeText(MainActivity.this, "READINGCONNECTABLE", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGFEATURE:
                            Log.e("tag", "READINGFEATURE");
                            Toast.makeText(MainActivity.this, "READINGFEATURE", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGFRAMES:
                            Log.e("tag", "READINGFRAMES");
                            Toast.makeText(MainActivity.this, "READINGFRAMES", Toast.LENGTH_SHORT).show();
                            break;
                        case READINGTRIGGERS:
                            Log.e("tag", "READINGTRIGGERS");
                            Toast.makeText(MainActivity.this, "READINGTRIGGERS", Toast.LENGTH_SHORT).show();
                            break;
                        case COMPLETED:
                            Log.e("tag", "COMPLETED");
                            Toast.makeText(MainActivity.this, "COMPLETED", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, MagazinDetail.class);
                            startActivity(intent);
                            break;
                        case CONNECTFAILED:
                        case DISCONNECTED:
                            Log.e("tag", "DISCONNECTED");
                            Toast.makeText(MainActivity.this, "DISCONNECTED", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });
        }

        @Override
        public void onError(MTException e) {
            Log.e("tag", e.getErrorCode() + " - " + e.getMessage());
        }
    };

    @Override
    public void onRequestPermissionsResult(int code, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(code, permissions, grantResults);
        switch (code) {
            case PERMISSION_COARSE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initData();
                } else {
                    finish();
                }
                break;
            case REQUEST_FINE_LOCATION:
                boolean isGrant = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false;
                        break;
                    }
                }
                if (isGrant) {
                    if (!isBLEEnabled()) {
                        showBLEDialog();
                    } else {
                        initData();
                    }
                }
                break;
        }
    }

    private void initData() {
        //The mobile phone system may restrict the scanning under the off-screen,
        // resulting in the inability to obtain broadcast data after the off-screen
        mtCentralManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mtCentralManager.stopService();
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
        if (id == R.id.addbeacon) {
            Intent i =new Intent(MainActivity.this, AppCodeScanner.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/
}