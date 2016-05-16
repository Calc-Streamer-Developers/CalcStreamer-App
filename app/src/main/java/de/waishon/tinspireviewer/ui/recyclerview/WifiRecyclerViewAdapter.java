package de.waishon.tinspireviewer.ui.recyclerview;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.waishon.tinspireviewer.R;

/**
 * Created by Sören on 08.03.2016.
 */
public class WifiRecyclerViewAdapter extends RecyclerView.Adapter<WifiViewHolder> {

    private List<ScanResult> wifiNetworkSSIDs = new ArrayList<ScanResult>();
    private WifiViewHolder.WifiViewHolderClickListener wifiViewHolderClickListener;

    public WifiRecyclerViewAdapter() {

    }

    public WifiRecyclerViewAdapter(List<ScanResult> wifiNetworkSSIDs, WifiViewHolder.WifiViewHolderClickListener wifiViewHolderClickListener) {
        this.wifiNetworkSSIDs = wifiNetworkSSIDs;
        this.wifiViewHolderClickListener = wifiViewHolderClickListener;
        this.notifyDataSetChanged();
    }

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WifiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_wlan_item, parent, false), wifiViewHolderClickListener);
    }

    @Override
    public void onBindViewHolder(WifiViewHolder holder, int position) {
        String wifiSSID = wifiNetworkSSIDs.get(position).SSID;
        holder.ssid.setText(wifiSSID);
    }

    @Override
    public int getItemCount() {
        return wifiNetworkSSIDs.size();
    }

    public List<ScanResult> getItems() {
        return wifiNetworkSSIDs;
    }

}
