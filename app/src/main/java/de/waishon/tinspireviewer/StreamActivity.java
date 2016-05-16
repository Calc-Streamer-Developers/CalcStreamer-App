package de.waishon.tinspireviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.waishon.tinspireviewer.communication.ProtocolImplementation;
import de.waishon.tinspireviewer.communication.avahi.ServerDiscovery;
import de.waishon.tinspireviewer.communication.SocketClient;
import de.waishon.tinspireviewer.listener.AvahiServerFoundListener;
import de.waishon.tinspireviewer.listener.ProtocolResponseListener;
import de.waishon.tinspireviewer.util.SystemUiHider;


public class StreamActivity extends AppCompatActivity {

    // Fullscreen Settings
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 5000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    // Fullscreen Utils
    private Handler mHideHandler = new Handler();
    private SystemUiHider mSystemUiHider;

    // System-Services
    private WifiManager wifiManager;

    // UI
    private LinearLayout contentView;
    private CoordinatorLayout coordinatorLayout;
    private ImageView imageView;
    private boolean frameSnackbarShown = false;
    private Snackbar snackbar;

    // Avahi-Implementation
    private WifiManager.MulticastLock lock;
    private boolean hasFound = false;

    // Background Services
    private SocketClient socketClient;
    private ProtocolImplementation protocolImplementation;
    private ServerDiscovery serverDiscovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        contentView = (LinearLayout) findViewById(R.id.activity_stream_content);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_stream_coordinatorlayout);
        imageView = (ImageView) findViewById(R.id.activity_stream_imageview);

        // Add Wifi Manager
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        lock = wifiManager.createMulticastLock(getClass().getName());
        lock.setReferenceCounted(true);
        lock.acquire();

        // Search for TI-Server
        serverDiscovery = new ServerDiscovery(this, wifiManager, new AvahiServerFoundListener() {

            // If found
            @Override
            public void found(String hostname, String ip, int port) {
                Log.i("TI", "FOUND");
                hasFound = true;
                showSnackbar("Warte auf TI...");

                // Start new Socket Client
                try {
                    protocolImplementation = new ProtocolImplementation((socketClient = new SocketClient(ip, port)), StreamActivity.this, new ProtocolResponseListener() {
                        @Override
                        public void unsupportedDevice() {
                            showSnackbar("Angeschlossener TI wird nicht unterstützt");
                        }

                        @Override
                        public void deviceConnected(String deviceName) {
                            showSnackbar("Neuer \"" + deviceName + "\" verbunden. Bitte warten...");
                        }

                        @Override
                        public void deviceDisconnected() {
                            showSnackbar("Verbindung zum TI wurde getrennt");
                        }

                        @Override
                        public void frameReceived(Bitmap bitmap) {
                            if (!frameSnackbarShown) {
                                (snackbar = Snackbar.make(coordinatorLayout, "Erfolgreich verbunden", Snackbar.LENGTH_SHORT)).show();
                                frameSnackbarShown = true;
                            }

                            imageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void connectionLost() {
                            showSnackbar("Verbindung zum Server verloren. Es wird erneut versucht");
                        }
                    });
                } catch (IOException e) {
                    showSnackbar("Es konnte keine Verbindung zum Server hergestellt werden.");
                }
            }

            @Override
            public void remove(String hostname, String ip, int port) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!hasFound) {
                    showSnackbar("Es konnte kein Server gefunden werden. Es wird erneut versucht ");
                }
            }
        }, 2000);

        // Fullscreen Animation Stuff
        contentView.setOnTouchListener(mDelayHideTouchListener);

        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
            @Override
            public void onVisibilityChange(boolean visible) {
                if (visible && AUTO_HIDE) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
    }


    private void showSnackbar(String message) {
        if (snackbar != null && snackbar.isShown()) snackbar.dismiss();
        (snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)).show();
        frameSnackbarShown = false;
    }

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    private View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}


