package ru.main.sabal.materialapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public abstract class Connector implements Sensor {

    public InputStream Inputstream;

    public BluetoothDevice getDevice() {
        return device;
    }

    public BluetoothDevice device;
    public BluetoothAdapter bluetooth;
    public BluetoothSocket socket;

    public InputStream getInputstream() {
        return Inputstream;
    }
    public BluetoothSocket getSocket() {
        return socket;
    }
    public boolean Connect(String name) throws IOException {

        bluetooth = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> DevList = bluetooth.getBondedDevices();

        for (BluetoothDevice i : DevList) {
            if (i.getName().equals(name)) device = i;
        }
        if (device == null) {
            return false;
        }

        socket = device.createInsecureRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
        socket.connect();
        Inputstream = socket.getInputStream();

        return Inputstream != null;
    }

    public void Disconnect () throws IOException {
        socket.close();
    }
}