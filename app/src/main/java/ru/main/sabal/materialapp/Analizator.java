package ru.main.sabal.materialapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by skb admin 14 on 24.08.2016.
 */
public class Analizator extends AppCompatActivity implements View.OnClickListener {
    public Connector Controller;
    boolean BTconnected = false;
    public InputStream inputStream;
    public BluetoothAdapter bluetooth;
    BluetoothSocket socket;
    private BluetoothSocket btSocket = null;
    boolean isAnalize = false;
    TextView textView;
    //private static BluetoothResponseHandler mHandler;
    Handler bluetoothIn;

    private Analizator.ConnectedThread mConnectedThread;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analizator);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Controller = new Arduino();
        String Device = "";
            Device = getIntent().getExtras().getString("Device Name", "null");

        this.textView = (TextView) findViewById(R.id.text_current_id);
        textView.setText("nkdfl");
        btSocket = Controller.getSocket();

        mConnectedThread = new Analizator.ConnectedThread(btSocket);
        mConnectedThread.start();
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");
                                    // determine the end-of-line
                    textView.setText("nkdfldfsfvbf");
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        textView.setText(recDataString + dataInPrint);
                        /*if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);

                            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
                            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
                            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        }*/
                        //recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };
    }

    void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuilder msg = new StringBuilder();

        msg.append(message);
        if (outgoing) msg.append('\n');
        textView.append(msg);

        final int scrollAmount = textView.getLayout().getLineTop(textView.getLineCount()) - textView.getHeight();
        if (scrollAmount > 0)
            textView.scrollTo(0, scrollAmount);
        else textView.scrollTo(0, 0);

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

    /*private static class BluetoothResponseHandler extends Handler {
        private WeakReference<Analizator> mActivity;

        public BluetoothResponseHandler(Analizator activity) {
            mActivity = new WeakReference<Analizator>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Analizator activity = mActivity.get();
            if (activity != null) {

                final String readMessage = (String) msg.obj;
                if (readMessage != null) {
                    activity.appendLog(readMessage, false, false, false);
                }

            }
        }
    }*/
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, device.getUuids()[0].getUuid());
            } catch (Exception e) {
                ToScreen("Could not create Insecure RFComm Connection");
            }
        }
        return  device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
    }
    private class ConnectedThread extends Thread {
       // private final InputStream mmInStream;
        //private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            /*InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }*/

            //mmInStream = tmpIn;
            //mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = Controller.getInputstream().read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
    public void ToScreen(String stroka) {
        Toast.makeText(getApplicationContext(), stroka, Toast.LENGTH_SHORT).show();
    }
}
