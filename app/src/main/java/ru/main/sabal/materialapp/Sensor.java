package ru.main.sabal.materialapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;

public interface Sensor {

    boolean Connect(String Name) throws IOException;
    void Disconnect() throws IOException;
    InputStream getInputstream();
    BluetoothDevice getDevice();
    BluetoothSocket getSocket();

}