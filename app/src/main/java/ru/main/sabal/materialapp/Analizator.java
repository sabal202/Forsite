package ru.main.sabal.materialapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by skb admin 14 on 24.08.2016.
 */
public class Analizator extends AppCompatActivity implements View.OnClickListener {
    public Sensor Controller;
    boolean BTconnected = false;
    public InputStream inputStream;
    public BluetoothAdapter bluetooth;
    BluetoothSocket socket;
    boolean isAnalize = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analizator);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Controller = new Arduino();
        String Device = "";
        if (getIntent().hasExtra("Device Name")) {
            Device = getIntent().getExtras().getString("Device Name", "null");
            try {
                BTconnected = Controller.Connect(Device);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (BTconnected) {

        } else {
            ToScreen("\nNo Bluetooth connection with " + Device + "\n");
            Analizator.this.finish();
        }
        final Handler handler = new Handler();
        class MyRunnable implements Runnable {
            private Handler handler;
            private int i;
            TextView textView = (TextView) findViewById(R.id.text_current_id);

            public MyRunnable(Handler handler, TextView textView) {
                this.handler = handler;
                this.textView = textView;
            }

            @Override
            public void run() {
                this.handler.postDelayed(this, 500);
                int bytes;
                int availableBytes = 0;
                    try {
                        availableBytes = inputStream.available();
                        if (availableBytes > 0) {
                            byte[] buffer = new byte[availableBytes];  // buffer store for the stream
                            // Read from the InputStream


                            bytes = inputStream.read(buffer);
                            this.textView.setText(textView.getText() + "\n" + new String(buffer));
                            handler.post(new MyRunnable(handler, textView));
                        }
                    } catch (IOException e) {
                        Log.d("Error reading", e.getMessage());
                        e.printStackTrace();

                    }


            }
        }

        /*new Thread( new Runnable(){
            @Override
            public void run(){
                Looper.prepare();
                //do work here
            }
        }).start();*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.analizator_button_start_stop:
                isAnalize = !isAnalize;
                break;

            case R.id.analizator_button_clear:

                break;


            default:
                break;
        }
    }

    public void ToScreen(String stroka) {
        Toast.makeText(getApplicationContext(), stroka, Toast.LENGTH_SHORT).show();
    }
}
