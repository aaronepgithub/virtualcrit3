package com.example.virtualcrit30;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.TimeUnit;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private TextView mTextMessage;
    private TextView mValueTimer;
    private TextView mActiveTimer;

    private long startTime = 0;
    private long activeMillis = 0;
    private long totalMillis = 0;
    private long lastMillis = 0;

    private String settingsName = "TIM";
    private String settingsGPS = "OFF";

    public String rDistance = "0 MILES";
    public String rAvgSpeed = "0.0 MPH";

    BroadcastReceiver receiver;
    IntentFilter filter;


    private void init() {
        try {

            receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    String xtr = intent.getStringExtra("msg");
                    String xtrName = intent.getStringExtra("type");
                    Log.i(TAG, "BroadcastReceiver, onReceive: " + action + ",  " + xtr + ",  " + xtrName);


                    if(action.equals("MESSAGE") && xtrName.equals("messageBar")){
//                        Log.i(TAG, "onReceive: action:  " + action);
//                        Log.i(TAG, "onReceive: Type:  " + xtrName);
//                        Log.i(TAG, "onReceive: xtr:  " + xtr);

                        setMessageText(xtr);

                    }

                    if(action.equals("MESSAGE") && xtrName.equals("hr")){
//                        Log.i(TAG, "onReceive: action:  " + action);
//                        Log.i(TAG, "onReceive: Type:  " + xtrName);
//                        Log.i(TAG, "onReceive: xtr:  " + xtr);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tvHR = (TextView) findViewById(R.id.valueHeartrateBLE);
                                tvHR.setText(xtr);
                            }
                        });
                    }

                    if(action.equals("MESSAGE") && xtrName.equals("cad")){
//                        Log.i(TAG, "onReceive: action:  " + action);
//                        Log.i(TAG, "onReceive: xtr:  " + xtr);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tvCAD = (TextView) findViewById(R.id.valueCadenceBLE);
                                tvCAD.setText(xtr);
                            }
                        });
                    }

                    if(action.equals("MESSAGE") && xtrName.equals("spd")){
//                        Log.i(TAG, "onReceive: action:  " + action);
//                        Log.i(TAG, "onReceive: xtr:  " + xtr);

                        rDistance = intent.getStringExtra("distance");
                        rAvgSpeed = intent.getStringExtra("avgspeed");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tvSPD = (TextView) findViewById(R.id.valueSpeedBLE);
                                tvSPD.setText(xtr);

                                TextView mDistance = (TextView) findViewById(R.id.valueDistanceBLE);
                                mDistance.setText(intent.getStringExtra("distance"));

                                TextView mAvgSpeed = (TextView) findViewById(R.id.valueAverageSpeedBLE);
                                mAvgSpeed.setText(intent.getStringExtra("avgspeed"));

                                TextView mActiveTimeBle = (TextView) findViewById(R.id.valueActiveTimeBLE);
                                mActiveTimeBle.setText(Variables.getvTotalTimeSeconds());
                            }
                        });
                    }
                }
            };

            filter = new IntentFilter("MESSAGE");
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private int secondsPerRound = 30;
    private int currentRound = 1;

//WRITE ROUND DATA...


    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            totalMillis = System.currentTimeMillis() - startTime;
            Timer.setTotalMillis(totalMillis);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mValueTimer.setText(Timer.getTotalTimeString());
                    TextView t1 = findViewById(R.id.tvHeader1);
                    t1.setText(Timer.getTotalTimeString());
                }
            });

            if (Timer.getStatus() == 0 && lastMillis > 0) {
                activeMillis += (totalMillis - lastMillis);
                Timer.setActiveMillis(activeMillis);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActiveTimer.setText(Timer.getActiveTimeString());
                    }
                });
            }

            if (totalMillis > (currentRound * secondsPerRound * 1000)) {
                currentRound += 1;
                Log.i(TAG, "round " + (currentRound - 1) + " complete");
                setMessageText("ROUND " + (currentRound));
                //geoRoundSpd =
                //geoBleSpd
                //geoHR
                //PROCESS NEW ROUND
            }


            lastMillis = totalMillis;
            timerHandler.postDelayed(this, 1000);
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    changeState(0);
                    setMessageText("Home");
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    setMessageText("Dashboard");
                    changeState(1);
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    setMessageText("Notification");
                    return true;
            }
            return false;
        }
    };


    private int viewState = 0;
    private void changeState(int i) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ScrollView sv = findViewById(R.id.svSettings);
                LinearLayout ll = findViewById(R.id.llView);

                switch (viewState) {
                    case 0: {

                        if (i == 1) {
                            viewState = 1;
//                        mTextMessage.setVisibility(View.GONE);
                            ll.setVisibility(View.VISIBLE);
                            sv.setVisibility(View.GONE);
                        }

                        break;
                    }

                    case 1: {

                        if (i == 0) {
                            viewState = 0;
//                        mTextMessage.setVisibility(View.GONE);
                            ll.setVisibility(View.GONE);
                            sv.setVisibility(View.VISIBLE);
                        }

                        break;
                    }

                    default: {
                        Log.i(TAG, "default...");
                    }
                }
            }
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickStart(getCurrentFocus());
        Variables.setMessageBarValue("onCreate");
        mTextMessage = (TextView) findViewById(R.id.message);
        mValueTimer = findViewById(R.id.valueTimer);
        mActiveTimer = findViewById(R.id.activeTimer);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }




    @Override
    protected void onResume() {
        super.onResume();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView mDistance = (TextView) findViewById(R.id.valueDistanceBLE);
                mDistance.setText(Variables.getDistance());

                TextView mAvgSpeed = (TextView) findViewById(R.id.valueAverageSpeedBLE);
                mAvgSpeed.setText(Variables.getAvgSpeed());
            }
        });

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }




    public void clickName(View view) {
        Log.i(TAG, "clickName: ");
        inputName();
    }

    public void displayName(String n) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView mName = findViewById(R.id.valueName);
                mName.setText(n);
            }
        });
    }

    public void inputName() {
        final EditText txtUrl = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("SETTINGS")
                .setMessage("ENTER NAME")
                .setView(txtUrl)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        settingsName = txtUrl.getText().toString().toUpperCase();
                        Log.i(TAG, "settingsName:  " + settingsName);
                        displayName(settingsName);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


    private int nextMile = 1;
    private int calibratedWheelSize = 0;
    private int revsAtStartOfMile = 1;




    @SuppressLint("DefaultLocale")
    public void onLocationReceived(Location location) {
        Log.i(TAG, "onLocationReceived");
        arrLats.add(location.getLatitude());
        arrLons.add(location.getLongitude());

        if (arrLats.size() < 5) {
            oldLat = location.getLatitude();
            oldLon = location.getLongitude();
            oldTime = location.getTime();
        } else {
            Location.distanceBetween(oldLat, oldLon, location.getLatitude(), location.getLongitude(), results);

            if (results.length > 0) {

                //mPrinter("RESULTS[0]  " + results[0] * 0.000621371 +  "  MILES"); //AS MILES
                if (results[0] == 0) {
                    //mPrinter("NOTHING AT RESULTS[0] - RETURN");
                    return;
                }
                if (results[0] * 0.000621371 <= 0) {
                    //mPrinter("NO DISTANCE TRAVELED - RETURN");
                    return;
                }

                //OPT 1.  QUICKREAD GEO SPEED
                final double geoSpeedQuick = (double) location.getSpeed() * 2.23694;  //meters/sec to mi/hr
                Log.i(TAG, "onLocationReceived: QuickSpeedCalc: " + geoSpeedQuick);

                //OPT 2.  GEO SPEED, LONG VERSION
                Double gd = results[0] * 0.000621371;
                long gt = (location.getTime() - oldTime);  //MILLI
                Double geoSpeed = gd / ((double) gt / 1000 / 60 / 60);
                geoDistance += results[0] * 0.000621371;


                //        //NEED TO USE GEO TO DETERMINE 1 MILE!!!


//        if (geoDistance >= (double) nextMile) {
//            nextMile += 1;
//            int cal = Variables.getWheelRevPerMile() - revsAtStartOfMile;
//            if (cal > 1) {
//                calibratedWheelSize = (int) (1609344 / cal);
//                //DISPLAY THIS SOMEWHERE
//                Variables.setMessageBarValue(String.valueOf(calibratedWheelSize) + ",  CALIBRATED WHEELSIZE MM");
//            }
//            revsAtStartOfMile = Variables.getWheelRevPerMile();
//
//        }


                totalTimeGeo += (location.getTime() - oldTime);  //MILLI
                double ttg = totalTimeGeo;  //IN MILLI
                double geoAvgSpeed = geoDistance / (ttg / 1000.0 / 60.0 / 60.0);
                long millis = totalTimeGeo;
                @SuppressLint("DefaultLocale")
                final String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                //UPDATE UI WITH SPEED AND DISTANCE
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tvSpd = (TextView) findViewById(R.id.valueSpeedGPS);
                        tvSpd.setText(String.format("%.2f MPH", geoSpeedQuick));

                        TextView tvDst = (TextView) findViewById(R.id.valueDistanceGPS);
                        tvDst.setText(String.format("%.2f Miles", geoDistance));

                        TextView tvTime = (TextView) findViewById(R.id.valueActiveTimeGPS);
                        tvTime.setText(hms);

                        TextView tvAvgSpd = (TextView) findViewById(R.id.valueAverageSpeedGPS);
                        tvAvgSpd.setText(String.format("%.1f MPH", geoAvgSpeed));
                    }
                });



            }

            oldLat = location.getLatitude();
            oldLon = location.getLongitude();
            oldTime = location.getTime();
        }


    }

    private ArrayList<Double> arrLats = new ArrayList<>();
    private ArrayList<Double> arrLons = new ArrayList<>();
    private Double oldLat = 0.0;
    private Double oldLon = 0.0;
    private Double geoDistance = 0.0;
    private Double geoAvgSpeed = 0.0;
    private float[] results = new float[2];
    private long oldTime = 0;
    private long totalTimeGeo = 0;  //GPS MOVING TIME IN MILLI

    private void startGPS() {
        Log.i(TAG, "startGPS: ");
        //START GPS
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);


        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //CHANGE FROM WORKING ORIGINAL
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }

            private void onNewLocation(Location lastLocation) {
                ////Log.i(TAG, "onNewLocation: " + lastLocation.getSpeed());
                onLocationReceived(lastLocation);
            }
        };

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            //Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }

    }


    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocation;


    public void clickGPS(View view) {
        TextView mGPS = findViewById(R.id.valueGPS);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (settingsGPS.equals("OFF")) {
                    mGPS.setText("ON");
                    settingsGPS = "ON";
                    Log.i(TAG, "clickGPS: ON");
                    startGPS();
                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "GPS ON" , Toast.LENGTH_SHORT)
                            .show();
                } else {
                    mGPS.setText("OFF");
                    settingsGPS = "OFF";
                    Log.i(TAG, "clickGPS: OFF");
                    //STOP GPS
                    try {
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    } catch (Exception e){
                        Log.i(TAG, "Error,  DIDN'T STOP LOCATION");
                    }
                    //SHOWALERT
                    Toast.makeText(getApplicationContext(),
                            "GPS OFF" , Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    public void clickStart(View view) {
        Log.i(TAG, "clickStart: ");

        if (Timer.getStatus() == 99) {
            Log.i(TAG, "Start Timer - First Time");
            //mValueTimer.setText("00:00:00");
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            manageTimer(0);
        }

        if (Timer.getStatus() == 1) {
            Log.i(TAG, "Resume Timer");
            timerHandler.postDelayed(timerRunnable, 0);
            manageTimer(0);
        }

        if (Timer.getStatus() == 2) {
            Log.i(TAG, "ReStart Timer");
            //WILL NOT ALLOW THIS...
            //mValueTimer.setText("00:00:00");
            totalMillis = 0;
            lastMillis = 0;
            activeMillis = 0;
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            manageTimer(0);
        }


    }


    public void clickPause(View view) {
        Log.i(TAG, "clickPause: ");
        //CAN'T REMOVE CB, NEED ACTIVE TIME TO CONTINUE
        //timerHandler.removeCallbacks(timerRunnable);
        manageTimer(1);
    }

    public void clickEnd(View view) {
        Log.i(TAG, "clickEnd: ");
        timerHandler.removeCallbacks(timerRunnable);
        manageTimer(2);
    }

    private void manageTimer(int i) {
        Log.i(TAG, "manageTimer: " + i);

        switch (i) {
            case 0:
                Log.i(TAG, "manageTimer: Start");
                Timer.setStatus(0);
                Toast.makeText(getApplicationContext(),
                        "START" , Toast.LENGTH_SHORT)
                        .show();
                return;
            case 1:
                Log.i(TAG, "manageTimer: Pause");
                Timer.setStatus(1);
                Toast.makeText(getApplicationContext(),
                        "PAUSE" , Toast.LENGTH_SHORT)
                        .show();
                return;
            case 2:
                Log.i(TAG, "manageTimer: End");
                Timer.setStatus(2);
                Toast.makeText(getApplicationContext(),
                        "COMPLETE" , Toast.LENGTH_SHORT)
                        .show();
        }


    }

    public void clickBLE(View view) {
        Log.i(TAG, "clickBLE: SCAN FOR BLE DEVICES");
        onScanStart();

    }



    private int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final long SCAN_PERIOD = 3000;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothDevice deviceDiscovered;
    private BluetoothDevice deviceDiscoveredCSC;



    public void onScanStart() {
        Log.i(TAG, "SCANNING HR");
        deviceDiscovered = null;
        //setMessageText("SCANNING HR");

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "PROMPT FOR LOCATION ENABLED");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        Log.i(TAG, "CHECK FOR BT ENABLED");
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<>();
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString("0000180D-0000-1000-8000-00805f9b34fb"))
                    .build();
            filters.add(scanFilter);

            // Show Alert
            Toast.makeText(getApplicationContext(),
                    "SCANNING HR" , Toast.LENGTH_SHORT)
                    .show();

            //START SCAN
            Log.i(TAG, "START SCANNING HR");
            mLEScanner.startScan(filters, settings, mScanCallback);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                    Log.i(TAG, "run: STOP SCANNING");
                    //setMessageText("-");
                    deviceDiscovered = null;
                    preScanStartCSC();
                }
            }, SCAN_PERIOD);

        }
    } //END HR SCAN

    private void preScanStartCSC() {
        Log.i(TAG, "preScanStartCSC: ");
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Pause Complete");
                deviceDiscovered = null;
                onScanStartCSC();
            }
        }, 1000);

    }

    //START CSC SCAN
    public void onScanStartCSC() {
        Log.i(TAG, "SCANNING CSC");
        setMessageText("SCANNING CSC");

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }


        Log.i(TAG, "CHECK FOR BT ENABLED");
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<>();
            ScanFilter scanFilter2 = new ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString("00001816-0000-1000-8000-00805f9b34fb"))
                    .build();
            filters.add(scanFilter2);

            // Show Alert
            Toast.makeText(getApplicationContext(),
                    "SCANNING CSC" , Toast.LENGTH_SHORT)
                    .show();

            //START SCAN
            Log.i(TAG, "START SCANNING CSC");
            mLEScanner.startScan(filters, settings, mScanCallbackCSC);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallbackCSC);
                    Log.i(TAG, "run: STOP SCANNING CSC");
                    setMessageText("SCAN COMPLETE");

                    //SHOW DEVICES FOUND
                    deviceDiscovered = null;

                    if (devicesDiscoveredHR.size() + devicesDiscoveredCSC.size() == 0) {
                        //setMessageText("NO DEVICES FOUND");
                        Toast.makeText(getApplicationContext(),
                                "NO FOUND DEVICES" , Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        postScanPopup();
                    }

                }
            }, SCAN_PERIOD);

        }
    }  //END CSC SCAN




    //SCAN RESULT CB HR

    private ArrayList<BluetoothDevice> devicesDiscoveredHR = new ArrayList<>();
    private ArrayList<BluetoothDevice> devicesDiscoveredCSC = new ArrayList<>();
    private ArrayList<BluetoothDevice> devicesConnectedHR = new ArrayList<>();
    private ArrayList<BluetoothDevice> devicesConnectedCSC = new ArrayList<>();


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice deviceDiscovered = result.getDevice();

            if (deviceDiscovered.getName() == null) {
                Log.i(TAG, "onScanResult: isNull return");
                return;
            }

            if (devicesDiscoveredHR.contains(deviceDiscovered)) {
                Log.i(TAG, "onScanResult: Already in HR Device Array List, return");
                return;
            }

            Log.i(TAG, "onScanResult: New HR device");
            devicesDiscoveredHR.add(deviceDiscovered);
            Log.i(TAG, "onScanResult added HR: " + deviceDiscovered.getName());
            setMessageText("FOUND:  " + deviceDiscovered.getName());
            Log.i(TAG, "onScanResult: getSizeOfDevicesDiscoveredHR:  " + devicesDiscoveredHR.size());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "onBatchScanResults: " + results.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, "onScanFailed: " + errorCode);
        }
    };
    //END SCAN CB HR

    //SCAN CB CSC
    private ScanCallback mScanCallbackCSC = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice deviceDiscoveredCSC = result.getDevice();

            if (deviceDiscoveredCSC.getName() == null) {
                Log.i(TAG, "onScanResult: isNull return");
                return;
            }

            if (devicesDiscoveredHR.contains(deviceDiscoveredCSC)) {
                Log.i(TAG, "onScanResult: Already in HR Device Array List, its a Mio");
                return;
            }

            if (devicesDiscoveredCSC.contains(deviceDiscoveredCSC)) {
                Log.i(TAG, "onScanResult: Already in CSC Device Array List, return");
                return;
            }

            Log.i(TAG, "onScanResult: New CSC device");
            devicesDiscoveredCSC.add(deviceDiscoveredCSC);
            Log.i(TAG, "onScanResult added CSC: " + deviceDiscoveredCSC.getName());
            setMessageText("FOUND:  " + deviceDiscoveredCSC.getName());
            Log.i(TAG, "onScanResult: getSizeOfDevicesDiscoveredCSC:  " + devicesDiscoveredCSC.size());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "onBatchScanResults: " + results.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, "onScanFailed: " + errorCode);
        }
    };
    //END SCAN CB CSC


    //IF CONNECTED, DON'T ASK

    private void postScanPopup() {
//        Devices.setDevicesDiscoveredHR(devicesDiscoveredHR);

        Log.i(TAG, "postScanPopup");
        if (devicesDiscoveredHR.size() + devicesDiscoveredCSC.size() == 0) {
            //setMessageText("NO DEVICES FOUND");
            Toast.makeText(getApplicationContext(),
                    "NO FOUND DEVICES" , Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        for(BluetoothDevice d : devicesDiscoveredHR) {
            Log.i(TAG, "postScanPopup: attempt connect to " + d.getName());


            if (devicesConnectedHR.contains(d)) {
                Log.i(TAG, "postScanPopup: already connected to " + d.getName());
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth")
                    .setMessage("Connect to " + d.getName() + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.i(TAG, "onClick: YES, now call to connect to device " + d.getName());
                            //send connection request
                            devicesConnectedHR.add(d);
                            setBluetoothDeviceNames(d.getName().toUpperCase());
                        initManagerHR(d);

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.i(TAG, "onClick: NO");
                        }
                    }).show();
        }

        //NEED TO WAIT UNTIL HR CONNECTION IS COMPLETE OR TRUST THAT NORDIC WILL BUFFER
        postScanPopupCSC();
    }

    private hrManager hrM;
    private void initManagerHR(BluetoothDevice b) {
        hrM = new hrManager(this, b);
    }

    private cscManager cscM;
    private void initManagerCSC(BluetoothDevice b) {
        cscM = new cscManager(this, b);
    }

    private void postScanPopupCSC() {

        Log.i(TAG, "postScanPopupCSC Started");
        if (devicesDiscoveredCSC.size() == 0) {
            //setMessageText("NO MORE DEVICES");
            return;
        }


        for(BluetoothDevice d : devicesDiscoveredCSC) {
            Log.i(TAG, "postScanPopup: attempt connect to " + d.getName());

            if (devicesConnectedCSC.contains(d)) {
                Log.i(TAG, "postScanPopupCSC: already connected to " + d.getName());
                return;
            }

            if (devicesConnectedHR.contains(d)) {
                Log.i(TAG, "postScanPopupCSC (HR): already connected to " + d.getName());
                return;
            }



            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth")
                    .setMessage("Connect to " + d.getName() + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.i(TAG, "onClick: YES, now call to connect to device");
                            Log.i(TAG, "onClick: " + d.getName());
                            //send connection request
                            devicesConnectedCSC.add(d);
                            setBluetoothDeviceNames(d.getName().toUpperCase());
                        initManagerCSC(d);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.i(TAG, "onClick: YES, now call to connect to device");
                        }
                    }).show();
        }
        Log.i(TAG, "postScanPopupCSC: loop complete, hope nordic buffers");

    }





    private void setBluetoothDeviceNames (String x) {
        Log.i(TAG, "setBluetoothDeviceNames: " + x);
        runOnUiThread(() -> {
            TextView tv1 = findViewById(R.id.valueBluetoothDevice1);
            TextView tv2 = findViewById(R.id.valueBluetoothDevice2);
            TextView tv3 = findViewById(R.id.valueBluetoothDevice3);
            if (tv1.getText().equals("")) {
                Log.i(TAG, "setBluetoothDeviceNames: tv1");
                tv1.setText(x);
                return;
            }
            if (tv2.getText().equals("")) {

                tv2.setText(x);
                return;
            }
            if (tv3.getText().equals("")) {
                tv3.setText(x);
            }
            Log.i(TAG, "setBluetoothDeviceNames: all name slots taken");

        });
    }

    private void setMessageText (String x) {
        runOnUiThread(() -> mTextMessage.setText(x));
    }

    public void clickWheelSize(View view) {

        runOnUiThread(() -> {

            TextView tv = findViewById(R.id.valueWheelSize);
            switch (Variables.getWheelSizeInMM()) {
                case 2105: {
                    tv.setText("WHEEL SIZE: 700X28");
                    Variables.setWheelSizeInMM(2136);
                    break;
                }
                case 2136: {
                    tv.setText("WHEEL SIZE: 700X32");
                    Variables.setWheelSizeInMM(2155);
                    break;
                }
                case 2155: {
                    tv.setText("WHEEL SIZE: 700X42");
                    Variables.setWheelSizeInMM(2224);
                    break;
                }
                case 2224: {
                    tv.setText("WHEEL SIZE: 700X25");
                    Variables.setWheelSizeInMM(2105);
                    break;
                }
                default: {
                    tv.setText("WHEEL SIZE: 700X25");
                    Variables.setWheelSizeInMM(2105);
                }
            }
            Log.i(TAG, "clickWheelSize: new size: " + Variables.getWheelSizeInMM());

        });



    }

    public void clickAudio(View view) {
        Log.i(TAG, "clickAudio: ...");
    }

    public void clickMessageBar(View view) {
        Log.i(TAG, "clickMessageBar: ");
        setMessageText(Variables.getMessageBarValue());
    }
}
