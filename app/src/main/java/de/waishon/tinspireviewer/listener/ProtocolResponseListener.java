package de.waishon.tinspireviewer.listener;

import android.graphics.Bitmap;

/**
 * Created by Sören on 09.03.2016.
 */
public interface ProtocolResponseListener {
    void unsupportedDevice();
    void deviceConnected(String deviceName);
    void deviceDisconnected();
    void frameReceived(Bitmap bitmap);
    void connectionLost();
}
