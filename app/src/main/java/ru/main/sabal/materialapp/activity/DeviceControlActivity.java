package ru.main.sabal.materialapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import ru.main.sabal.materialapp.DeviceData;
import ru.main.sabal.materialapp.R;
import ru.main.sabal.materialapp.Utils;
import ru.main.sabal.materialapp.bluetooth.DeviceConnector;
import ru.main.sabal.materialapp.bluetooth.DeviceListActivity;


public final class DeviceControlActivity extends BaseActivity implements TextToSpeech.OnInitListener {
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";

    //private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;
    ImageView Beacon1, Beacon2, Beacon3, Beacon4, Beacon5, Beacon6;
    private static DeviceConnector connector;
    private static BluetoothResponseHandler mHandler;
    private TextToSpeech mTTS;
    private TextView logTextView;
    private boolean hexMode, needClean;
    //private boolean show_timings, show_direction;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.alert_about_tittle)
                .setMessage("Текущая версия приложения оптимизирована для смартфонов с диагональю 4.8 дюйма или близкие к этому")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.alert_about_ok, myClickListener)
                .create().show();
        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);
        mTTS = new TextToSpeech(this, this);
        setContentView(R.layout.activity_terminal);
        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

        this.logTextView = (TextView) findViewById(R.id.log_textview);
        this.logTextView.setMovementMethod(new ScrollingMovementMethod());
        if (savedInstanceState != null)
            logTextView.setText(savedInstanceState.getString(LOG));

        Beacon1 = (ImageView) findViewById(R.id.BeaconImage1);
        Beacon2 = (ImageView) findViewById(R.id.BeaconImage2);
        Beacon3 = (ImageView) findViewById(R.id.BeaconImage3);
        Beacon4 = (ImageView) findViewById(R.id.BeaconImage4);
        Beacon5 = (ImageView) findViewById(R.id.BeaconImage5);
        Beacon6 = (ImageView) findViewById(R.id.BeaconImage6);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    dialog.cancel();
                    break;
            }
        }

    };

    @Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEVICE_NAME, deviceName);
        if (logTextView != null) {
            final String log = logTextView.getText().toString();
            outState.putString(LOG, log);
        }
    }

    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }

    private void stopConnection() {
        if (connector != null) {
            connector.stop();
            connector = null;
            deviceName = null;
        }
    }


    private void startDeviceListActivity() {
        stopConnection();
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    public boolean onSearchRequested() {
        if (super.isAdapterReady()) startDeviceListActivity();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_control_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;

            case R.id.menu_clear:
                if (logTextView != null) logTextView.setText("");
                return true;

            case R.id.menu_send:
                if (logTextView != null) {
                    final String msg = logTextView.getText().toString();
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
                }
                return true;

            case R.id.menu_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //this.show_timings = Utils.getBooleanPrefence(this, getString(R.string.pref_log_timing));
        //this.show_direction = Utils.getBooleanPrefence(this, getString(R.string.pref_log_direction));
        this.needClean = Utils.getBooleanPrefence(this, getString(R.string.pref_need_clean));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (super.isAdapterReady() && (connector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                super.pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Utils.log("BT not enabled");
                }
                break;
        }
    }

    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);
            connector.connect();
        } catch (IllegalArgumentException e) {
            Utils.log("setupConnector failed: " + e.getMessage());
        }
    }

    ArrayList<String> log = new ArrayList<>();

    void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {
        if (log.size() == 0) {
            log.add("0");
        }
        StringBuilder msg = new StringBuilder();
        /*if (show_timings) msg.append("[").append(timeformat.format(new Date())).append("]");
        if (show_direction) {
            final String arrow = (outgoing ? " << " : " >> ");
            msg.append(arrow);
        } else msg.append(" ");*/

        msg.append(hexMode ? Utils.printHex(message) : message);
        //if (outgoing) msg.append('\n');
        String[] numbers = msg.toString().split("\n");

        if (msg.toString().equals(log.get(log.size() - 1))) {

        } else {
            log.add(msg.toString());
            logTextView.setText(log.get(log.size() - 1));
            int IDbe = Character.getNumericValue(numbers[0].charAt(0)) * 1000 + Character.getNumericValue(numbers[0].charAt(1)) * 100 + Character.getNumericValue(numbers[0].charAt(2)) * 10 + Character.getNumericValue(numbers[0].charAt(3));
            switch (IDbe) {
                case 1235:
                    disableBeacons();
                    Beacon1.setBackgroundResource(R.drawable.beacon);
                    mTTS.speak(getString(R.string.beacon1), TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case 1236:
                    disableBeacons();
                    Beacon2.setBackgroundResource(R.drawable.beacon);
                    mTTS.speak(getString(R.string.beacon2), TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case 1234:
                    disableBeacons();
                    Beacon3.setBackgroundResource(R.drawable.beacon);
                    mTTS.speak(getString(R.string.beacon3), TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case 1237:
                    disableBeacons();
                    Beacon4.setBackgroundResource(R.drawable.beacon);
                    mTTS.speak(getString(R.string.beacon4), TextToSpeech.QUEUE_FLUSH, null);
                    break;
                default:
                    Toast.makeText(this, "чет не распознал" + numbers[0].charAt(0) + "  " + numbers[0].charAt(1) + "  " + numbers[0].charAt(2) + "  " + numbers[0].charAt(3) + IDbe + "  " + msg.toString() + "  " + numbers[0].length() + "  " + numbers[0], Toast.LENGTH_LONG).show();
                    disableBeacons();
                    break;
            }
        }


        /*final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
        if (scrollAmount > 0)
            logTextView.scrollTo(0, scrollAmount);
        else logTextView.scrollTo(0, 0);*/
    }

    private void disableBeacons() {
        Beacon1.setBackgroundResource(R.drawable.beacon_nonselected);
        Beacon2.setBackgroundResource(R.drawable.beacon_nonselected);
        Beacon3.setBackgroundResource(R.drawable.beacon_nonselected);
        Beacon4.setBackgroundResource(R.drawable.beacon_nonselected);
        Beacon5.setBackgroundResource(R.drawable.beacon_nonselected);
        Beacon6.setBackgroundResource(R.drawable.beacon_nonselected);
    }

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
    }

    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<DeviceControlActivity> mActivity;

        public BluetoothResponseHandler(DeviceControlActivity activity) {
            mActivity = new WeakReference<DeviceControlActivity>(activity);
        }

        public void setTarget(DeviceControlActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<DeviceControlActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceControlActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:

                        Utils.log("MESSAGE_STATE_CHANGE: " + msg.arg1);
                        final ActionBar bar = activity.getSupportActionBar();
                        switch (msg.arg1) {
                            case DeviceConnector.STATE_CONNECTED:
                                bar.setSubtitle(MSG_CONNECTED);
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                bar.setSubtitle(MSG_CONNECTING);
                                break;
                            case DeviceConnector.STATE_NONE:
                                bar.setSubtitle(MSG_NOT_CONNECTED);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        final String readMessage = (String) msg.obj;
                        if (readMessage != null) {
                            activity.appendLog(readMessage, false, false, activity.needClean);
                        }
                        break;

                    case MESSAGE_DEVICE_NAME:
                        activity.setDeviceName((String) msg.obj);
                        break;

                    case MESSAGE_WRITE:
                        // stub
                        break;

                    case MESSAGE_TOAST:
                        // stub
                        break;
                }
            }
        }
    }
}