package de.waishon.tinspireviewer.listener;

/**
 * Created by Sören on 09.03.2016.
 */
public interface AvahiServerFoundListener {
    void found(String hostname, String ip, int port);
    void remove(String hostname, String ip, int port);
}
