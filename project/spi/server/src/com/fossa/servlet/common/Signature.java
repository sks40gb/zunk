/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

import java.io.Serializable;

/**
 *
 * @author Bala
 */

public class Signature implements Serializable{

    private int fastSum;
    private byte[] strongSum;

    public Signature(int fastSum, byte[] strongSum) {
        this.fastSum = fastSum;
        this.strongSum = strongSum;
    }

    public int getFastSum() {
        return fastSum;
    }

    public byte[] getStrongSum() {
        return strongSum;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Signature[");
        buffer.append(Integer.toHexString(fastSum));
        buffer.append(";");
        for (int i = 0; i < strongSum.length; i++) {
            int ss = ((int) strongSum[i]) & 0xFF;
            if (ss < 0x10) {
                buffer.append('0');
            }
            buffer.append(Integer.toHexString(ss));
        }
        buffer.append(']');
        return buffer.toString();
    }
}