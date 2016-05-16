package de.waishon.tinspireviewer.communication.avahi;

/**
 * Created by Sören on 18.01.2016.
 */
public class ServiceInformationState {
    private String hostname;
    private String ip;
    private int port;

    public ServiceInformationState(String hostname, String ip, int port) {
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
