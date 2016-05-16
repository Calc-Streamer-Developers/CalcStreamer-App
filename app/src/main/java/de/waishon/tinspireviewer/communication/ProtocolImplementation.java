package de.waishon.tinspireviewer.communication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import de.waishon.tinspireviewer.listener.ProtocolResponseListener;

/**
 * Created by Sören on 09.03.2016.
 */
public class ProtocolImplementation extends Transmission implements Runnable {

    private ProtocolResponseListener protocolResponseListener;
    private Activity activity;

    private Bitmap screen;
    private DisplayMetrics displayMetrics;

    /**
     * Constructor
     */
    public ProtocolImplementation(SocketClient socketClient, Activity activity, ProtocolResponseListener protocolResponseListener) throws IOException {
        super(socketClient.getInputStream(), socketClient.getOutputStream());

        this.activity = activity;
        this.protocolResponseListener = protocolResponseListener;

        this.displayMetrics = activity.getApplicationContext().getResources().getDisplayMetrics();

        run();
    }

    public void stop() {
        this.stop();
    }

    /**
     * Runs in Thread
     */
    @Override
    public void run() {
        try {
            while (true) {
                byte[] responsePackage = receivePackage();

                switch (responsePackage[0]) {
                    case 1:
                        protocolResponseListener.unsupportedDevice();
                        break;

                    case 2:
                        protocolResponseListener.deviceConnected(new String(Arrays.copyOfRange(responsePackage, 1, responsePackage.length)));
                        break;

                    case 3:
                        this.receiveFrame(responsePackage);
                        break;

                    case 4:
                        protocolResponseListener.deviceDisconnected();
                        break;
                }
            }
        } catch (IOException e) {
            protocolResponseListener.connectionLost();
        }
    }

    /**
     * Receives a frame and calls the listener
     *
     * @param responsePackage
     * @throws IOException
     */
    private void receiveFrame(byte[] responsePackage) throws IOException {
        long totalSize = ((long) (responsePackage[1] & 0xFF) << 24) +
                ((long) (responsePackage[2] & 0xFF) << 16) +
                ((long) (responsePackage[3] & 0xFF) << 8) +
                ((long) (responsePackage[4] & 0xFF));

        long receivedBytes = 0;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (receivedBytes < totalSize) {
            byte[] frameData = receivePackage();
            byteArrayOutputStream.write(frameData);

            receivedBytes += frameData.length;
        }

        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        screen = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        screen = Bitmap.createScaledBitmap(screen, screen.getWidth() * (displayMetrics.heightPixels / screen.getHeight()), displayMetrics.heightPixels, false);

        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                protocolResponseListener.frameReceived(screen);
            }
        });

    }
}
