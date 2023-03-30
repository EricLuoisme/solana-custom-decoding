package com.solana.custom.utils.atom;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import static org.bitcoinj.core.Utils.reverseBytes;

/**
 * Originally from SolanaJ, here is for jdk8 version
 */
public class ByteUtils {
    public static final int UINT_32_LENGTH = 4;
    public static final int UINT_64_LENGTH = 8;

    public static byte[] readBytes(byte[] buf, int offset, int length) {
        byte[] b = new byte[length];
        System.arraycopy(buf, offset, b, 0, length);
        return b;
    }

    public static BigInteger readUint64(byte[] buf, int offset) {
        return new BigInteger(reverseBytes(readBytes(buf, offset, UINT_64_LENGTH)));
    }

    public static void uint64ToByteStreamLE(BigInteger val, OutputStream stream) throws IOException {
        byte[] bytes = val.toByteArray();
        if (bytes.length > 8) {
            if (bytes[0] == 0) {
                bytes = readBytes(bytes, 1, bytes.length - 1);
            } else {
                throw new RuntimeException("Input too large to encode into a uint64");
            }
        }
        bytes = reverseBytes(bytes);
        stream.write(bytes);
        if (bytes.length < 8) {
            for (int i = 0; i < 8 - bytes.length; i++)
                stream.write(0);
        }
    }


    /**
     * Trim empty right
     *
     * @param array byte array
     * @return compact array
     */
    public static byte[] trimRight(byte[] array) {
        int i = array.length - 1;
        while (array[i] == 0 && i >= 0) {
            i--;
        }
        byte[] trimArr = new byte[i + 1];
        System.arraycopy(array, 0, trimArr, 0, i + 1);
        return trimArr;
    }

}
