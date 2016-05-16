package de.waishon.tinspireviewer.communication.avahi;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;


import de.waishon.tinspireviewer.listener.AvahiServerFoundListener;
import de.waishon.tinspireviewer.util.Utils;

/**
 * Created by Sören on 18.01.2016.
 */
public class ServerDiscovery implements ServiceListener {

    public static final String SERVICE_TYPE = "_tiviewer._tcp.local.";

    private JmDNS jmDNS;

    private Activity activity;
    private WifiManager wifiManager;
    private boolean useIPv6 = false;
    private AvahiServerFoundListener listener;

    public ServerDiscovery(Activity activity, WifiManager wifiManager, AvahiServerFoundListener listener) {
        this.activity = activity;
        this.wifiManager = wifiManager;
        this.listener = listener;
        new BackgroundDiscovery().start();
    }

    public ServerDiscovery(Activity activity, WifiManager wifiManager, boolean useIPv6, AvahiServerFoundListener listener) {
        this.activity = activity;
        this.wifiManager = wifiManager;
        this.useIPv6 = useIPv6;
        this.listener = listener;
        new BackgroundDiscovery().start();
    }

    class BackgroundDiscovery extends Thread {

        @Override
        public void run() {
            try {
                if(useIPv6)
                    jmDNS = JmDNS.create();
                else

                    jmDNS = JmDNS.create(Inet4Address.getByAddress(Utils.toIPByteArray(wifiManager.getConnectionInfo().getIpAddress())));

                jmDNS.addServiceListener(SERVICE_TYPE, ServerDiscovery.this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void serviceAdded(ServiceEvent serviceEvent) {
        Log.i("TI", "FOUND!" + serviceEvent.getInfo().toString());
    }

    @Override
    public void serviceRemoved(ServiceEvent serviceEvent) {
        listener.remove(serviceEvent.getName(), getRootIP(serviceEvent.getInfo().getInetAddresses()).getHostAddress(), serviceEvent.getInfo().getPort());
    }

    @Override
    public void serviceResolved(ServiceEvent serviceEvent) {
        Log.i("TI", "Resolve" + getRootIP(serviceEvent.getInfo().getInetAddresses()).getHostAddress());

        listener.found(serviceEvent.getName(), getRootIP(serviceEvent.getInfo().getInetAddresses()).getHostAddress(), serviceEvent.getInfo().getPort());
    }

    public InetAddress getRootIP(InetAddress[] addresses) {
        InetAddress rootAdress = null;

        for(InetAddress address : addresses) {
            if(!useIPv6 && address instanceof Inet4Address) {
                rootAdress = address;
                break;
            } else if(useIPv6 && address instanceof Inet6Address) {
                rootAdress = address;
                break;
            }
        }

        return rootAdress;
    }
}
