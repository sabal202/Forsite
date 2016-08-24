package ru.main.sabal.materialapp;

import java.io.IOException;

public interface Sensor {

    boolean Connect(String Name) throws IOException;
    void Disconnect() throws IOException;

}