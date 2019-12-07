package com.util;

import java.util.BitSet;

/**
 * @author lala
 */
public class Byte2Bitset {
    public static BitSet fromByteArrayReverse(final byte[] bytes) {

        final BitSet bits = new BitSet();
        int bytesLength = bytes.length * 8;
        for (int i = 0; i < bytesLength; i++) {
            if ((bytes[i / 8] & (1 << (7 - (i % 8)))) != 0) {
                bits.set(i);
            }
        }
        return bits;
    }

}
