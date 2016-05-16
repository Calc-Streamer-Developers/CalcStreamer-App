package de.waishon.tinspireviewer.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Sören on 09.03.2016.
 */
public class SocketClient {

    private Socket socket;

    public SocketClient(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
