package rdt.client.fileSystem;
import rdt.util.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MyByteBuffer {

    private ArrayList<Byte> bytes;
    private int numByte;

    public MyByteBuffer(byte[] bytesLocal) {
        this.bytes = new ArrayList<Byte>(bytesLocal.length);
        for (int i = 0; i < bytesLocal.length; i++) {
            this.bytes.add(bytesLocal[i]);
        }
        this.numByte = 0;
    }

    public MyByteBuffer(ArrayList<Byte> bytesLocal) {
        this.bytes = bytesLocal;
        this.numByte = 0;
    }

    public MyByteBuffer(){
        bytes = new ArrayList<>();
        numByte = 0;
    }

    public byte[] getBytes() {
        byte[] bytesLocal = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bytesLocal[i] = this.bytes.get(i);
        }
        return bytesLocal;
    }

    public void addInt(int i) {
        this.bytes.add((byte)(i >> 24));
        this.bytes.add((byte)(i >> 16));
        this.bytes.add((byte)(i >> 8));
        this.bytes.add((byte)i);
    }

    public int getInt() {
        byte    a1 = bytes.get(numByte++),
                a2 = bytes.get(numByte++),
                a3 = bytes.get(numByte++),
                a4 = bytes.get(numByte++);
        int i = (a4 & 0xFF) | ((a3 & 0xFF) << 8) | ((a2 & 0xFF) << 16) | ((a1 & 0xFF) << 24);
        return i;
    }

    public void addIntArray(int[] ints){
        addInt(ints.length);
        for (int i = 0; i < ints.length; i++) {
            addInt(ints[i]);
        }
    }

    public int[] getIntArray(){
        int size = getInt();
        int[] ints = new int[size];
        for (int i = 0; i < size; i++) {
            ints[i] = getInt();
        }
        return ints;
    }

    public void addFloat(float f) {
        bytes.add((byte)(Float.floatToIntBits(f) >> 24));
        bytes.add((byte)(Float.floatToIntBits(f) >> 16));
        bytes.add((byte)(Float.floatToIntBits(f) >> 8));
        bytes.add((byte) Float.floatToIntBits(f));
    }

    public float getFloat() {
        byte    a1 = bytes.get(numByte++),
                a2 = bytes.get(numByte++),
                a3 = bytes.get(numByte++),
                a4 = bytes.get(numByte++);
        int bits = (a4 & 0xFF) | ((a3 & 0xFF) << 8) | ((a2 & 0xFF) << 16) | ((a1 & 0xFF) << 24);
        float f = Float.intBitsToFloat(bits);
        return f;
    }

    public void addFloatArray(float[] f){
        addInt(f.length);
        for (int i = 0; i < f.length; i++) {
            addFloat(f[i]);
        }
    }

    public float[] getFloatArray() {
        int sizeOfArray = getInt();
        float[] f = new float[sizeOfArray];
        for (int i = 0; i < sizeOfArray; i++) {
            f[i] = getFloat();
        }
        return f;
    }

    public void addString(String s) {
        byte[] bytesLocal = new byte[0];
        try {
            bytesLocal = s.getBytes("UTF-16");
        } catch (UnsupportedEncodingException e) {
            Logger.logError(e);
        }
        addInt(bytesLocal.length);
        for (int i = 0; i < bytesLocal.length; i++) {
            bytes.add(bytesLocal[i]);
        }
    }

    public String getString() {
        int sizeOfArray = getInt();
        byte[] bytesLocal = new byte[sizeOfArray];
        for (int i = 0; i < sizeOfArray; i++) {
            bytesLocal[i] = bytes.get(numByte++);
        }
        String s = null;
        try {
            s = new String(bytesLocal, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            Logger.logError(e);
        }
        return s;
    }

    public void addStringArray(String[] s) {
        addInt(s.length);
        for (int i = 0; i < s.length; i++) {
            addString(s[i]);
        }
    }

    public String[] getStringArray() {
        int sizeOfArray = getInt();
        String[] s = new String[sizeOfArray];
        for (int i = 0; i < sizeOfArray; i++) {
            s[i] = getString();
        }
        return s;
    }
}
