package de.waishon.tinspireviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.waishon.tinspireviewer.ui.recyclerview.WifiRecyclerViewAdapter;
import de.waishon.tinspireviewer.ui.recyclerview.WifiViewHolder;

public class WelcomeActivity extends AppCompatActivity {

    public static final String TI_DEFAULT_WIFI_NAME = "TI-Nspire";

    // System elements
    private WifiManager wifiManager;
    private WifiNetworkReceiver wifiNetworkReceiver;
    private SharedPreferences sharedPreferences;

    // UI elements
    private RecyclerView recyclerView;
    private WifiRecyclerViewAdapter wifiRecyclerViewAdapter;
    private ProgressBar progressBar;
    private LinearLayout mainContent;
    private TextView progressText;

    private boolean isConnectionModeEnabled = false;

    public static WelcomeActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));

        this.instance = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        wifiRecyclerViewAdapter = new WifiRecyclerViewAdapter();

        progressBar = (ProgressBar) findViewById(R.id.activity_welcome_progressbar);
        progressText = (TextView) findViewById(R.id.activity_welcome_progres_text);
        mainContent = (LinearLayout) findViewById(R.id.activity_welcome_content);

        // WIFI-Device List
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_wlan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(wifiRecyclerViewAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Is Wifi active?
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        wifiNetworkReceiver = new WifiNetworkReceiver();

        // Register broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        registerReceiver(wifiNetworkReceiver, intentFilter);

        // Start Wifi Scan
        wifiManager.startScan();
        toggleProgressbar(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Einstellungen
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class WifiNetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                toggleProgressbar(false);

                List<ScanResult> scanResults = wifiManager.getScanResults();

                wifiRecyclerViewAdapter = new WifiRecyclerViewAdapter(scanResults, new ItemClickHandler());
                recyclerView.setAdapter(wifiRecyclerViewAdapter);

            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();

                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI & isConnectionModeEnabled) {
                    startActivity(new Intent(WelcomeActivity.this, StreamActivity.class));
                    WelcomeActivity.this.finish();
                } else {
                    toggleProgressbar(true);
                }
            }
        }
    }

    private class ItemClickHandler implements WifiViewHolder.WifiViewHolderClickListener {

        @Override
        public void onItemClick(int position) {
            // Is already connected with the right once => start new actvity
            if (wifiManager.getConnectionInfo().getSSID().equals("\"" + wifiRecyclerViewAdapter.getItems().get(position).SSID + "\"")) {

                startActivity(new Intent(WelcomeActivity.this, StreamActivity.class));
                WelcomeActivity.this.finish();

                return;
            }

            Log.i("TI", "WIFIINFO:" + wifiManager.getConnectionInfo().getSSID() + " -- List" + wifiRecyclerViewAdapter.getItems().get(position).SSID);

            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = String.format("\"%s\"", wifiRecyclerViewAdapter.getItems().get(position).SSID);
            wifiConfiguration.preSharedKey = String.format("\"%s\"", sharedPreferences.getString("wifi_password", "0"));

            wifiManager.disconnect();
            wifiManager.enableNetwork(wifiManager.addNetwork(wifiConfiguration), true);
            wifiManager.reconnect();

            toggleProgressbar(true);
            isConnectionModeEnabled = true;
        }
    }

    private void toggleProgressbar(boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            mainContent.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiNetworkReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiNetworkReceiver);
    }
}
