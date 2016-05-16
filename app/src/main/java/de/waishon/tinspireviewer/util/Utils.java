package de.waishon.tinspireviewer.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Eine Utility Klasse mit nützlichen Funktionen
 *
 * @author soeren
 */
public class Utils {

    /**
     * Wandelt ein Int zu einem ByteArray um.
     *
     * @param num
     * @return Der Integer als ByteArray
     */
    public static byte[] intToByteArray(int num) {
        byte[] data = {(byte) ((num >> 8) & 0xff), (byte) (num & 0xff)};
        return data;
    }

    public static byte[] toIPByteArray(int addr) {
        return new byte[]{(byte) addr, (byte) (addr >>> 8), (byte) (addr >>> 16), (byte) (addr >>> 24)};
    }

    public static InetAddress toInetAddress(int addr) {
        try {
            return InetAddress.getByAddress(toIPByteArray(addr));
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
