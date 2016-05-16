package de.waishon.tinspireviewer.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.waishon.tinspireviewer.R;

/**
 * Created by Sören on 08.03.2016.
 */
public class WifiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected TextView ssid;
    protected ImageView connectionStatusImage;

    private  WifiViewHolderClickListener clickListener;

    public WifiViewHolder(View itemView, WifiViewHolderClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;

        itemView.setOnClickListener(this);

        ssid = (TextView) itemView.findViewById(R.id.recyclerview_wlan_item_ssid);
        connectionStatusImage = (ImageView) itemView.findViewById(R.id.recyclerview_wlan_item_image);
    }

    @Override
    public void onClick(View v) {
        clickListener.onItemClick(getAdapterPosition());
    }

    public interface WifiViewHolderClickListener {
        void onItemClick(int position);
    }
}

