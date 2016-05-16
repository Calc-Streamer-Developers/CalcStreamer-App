package de.waishon.tinspireviewer.communication;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import de.waishon.tinspireviewer.util.Utils;

/**
 * Verwaltet die Socket Kommunikation auf Byte-Ebene
 *
 * @author soeren
 */
public abstract class Transmission {

    // InputStream
    private InputStream inputStream;

    // OutputStream
    private OutputStream outputStream;

    /**
     * Konsturktor
     * Initialisiert die Streams
     *
     * @param inputStream
     * @param outputStream
     */
    public Transmission(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Sendet ein Package als ByteArray
     *
     * @param data
     * @throws IOException
     * @see <a href="https://github.com/Drop-Project/Vala-Server/blob/master/PROTOCOL">PROTOCOL</a>
     */
    public void sendPackage(byte[] data) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        byte[] packageLength = Utils.intToByteArray(data.length);


        byteStream.write(packageLength);
        byteStream.write(data);
        outputStream.write(byteStream.toByteArray());
    }

    /**
     * Empfängt ein Package als ByteArray
     *
     * @return Das ByteArray vom Server
     * @throws IOException
     * @see <a href="https://github.com/Drop-Project/Vala-Server/blob/master/PROTOCOL">PROTOCOL</a>
     */
    public byte[] receivePackage() throws IOException {
        byte[] header = new byte[2];
        byte[] data = null;

        DataInputStream dataInputStream = new DataInputStream(inputStream);
        dataInputStream.readFully(header, 0, header.length);

        int packageLength = ((header[0] & 0xFF) << 8) + (header[1] & 0xFF);

        data = new byte[packageLength];
        dataInputStream.readFully(data, 0, data.length);

        return data;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

}
