package ru.main.sabal.materialapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech mTTS;
    public Sensor Controller;
    boolean BTconnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTTS = new TextToSpeech(this, this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
            MainActivity.this.finish();
        }

    }

    String message = "";
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setdevice) {
            Intent intent = new Intent(MainActivity.this, SetDevice.class);
            startActivity(intent);
            this.finish();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        switch (v.getId()){
            case R.id.button2:
                message = getString(R.string.alert_about_text1);
                mTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                //createAlert(adb);
                break;

            case R.id.button3:
                message = getString(R.string.alert_about_text2);
                mTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                //createAlert(adb);
                break;

            case R.id.button4:
                mTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                message = getString(R.string.alert_about_text3);
                //createAlert(adb);
                break;

            default:
                break;
        }
    }

    private void createAlert(AlertDialog.Builder adb) {
        adb.setTitle(R.string.alert_about_tittle)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.alert_about_ok, myClickListener)
                .create().show();
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    dialog.cancel();
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");

            //int result = mTTS.setLanguage(locale);
            int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            } else {
               // mButton.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Ошибка!");
        }

    }
    public void ToScreen(String stroka) {
        Toast.makeText(getApplicationContext(), stroka, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown mTTS!
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}
