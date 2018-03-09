package com.aaronep.andy.myplayground;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        localBroadcastReceiver = new LocalBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastReceiver,
                new IntentFilter("SOME_ACTION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastReceiver,
                new IntentFilter("OTHER_ACTION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastReceiver,
                new IntentFilter("B1_ACTION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastReceiver,
                new IntentFilter("B2_ACTION"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                localBroadcastReceiver);
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

            DialogFragment myFragment = new MyDialogFragment();
            myFragment.show(getFragmentManager(), "theDialog");

            return true;
        } else if (id == R.id.action_nothing) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onScan_Click(MenuItem item) {

    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // safety check
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals("SOME_ACTION")) {
                //doSomeAction();
                Log.i("TAG", "SOME ACTION onReceive");
            }
            if (intent.getAction().equals("OTHER_ACTION")) {
                //doSomeAction();
                Log.i("TAG", "OTHER_ACTION onReceive");
            }
            if (intent.getAction().equals("B1_ACTION")) {
                //doSomeAction();
                Log.i("TAG", "B1_ACTION onReceive");
            }
            if (intent.getAction().equals("B2_ACTION")) {
                //doSomeAction();
                Log.i("TAG", "B2_ACTION onReceive");
                sendToaster("B2 onReceive" + ", " + myString + ", " + myDouble);
            }
        }
    }

    private void sendToaster(String toasterText) {
        Toast.makeText(this,toasterText,Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver localBroadcastReceiver;
    private Double myDouble = 0.0;
    private String myString = "My String";



    public void onButton1_Click(View view) {

//        DialogFragment myFragment = new MyDialogFragment();
//        myFragment.show(getFragmentManager(), "theDialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("2 Button Dialog")
                .setCancelable(false)
                .setPositiveButton("B1", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("B1","B1 Clicked");

                        LocalBroadcastManager.getInstance(getParent()).sendBroadcast(
                                new Intent("B1_ACTION"));

                        dialog.cancel();

                    }
                })
                .setNegativeButton("B2", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("B2","B2 Clicked");
                        myDouble = 10.0;
                        myString = "A String";

                        LocalBroadcastManager.getInstance(getParent()).sendBroadcast(
                                new Intent("B2_ACTION"));

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

}