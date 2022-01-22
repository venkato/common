package com.jcraft.jsch;

import groovy.transform.CompileStatic;

import java.io.IOException;
import java.io.OutputStream;

@CompileStatic
public class JschBufferHelper {


    public static int getBufferIndex(Buffer b) { return b.index; }

    public static int getBufferS(Buffer b) { return b.s; }

    public static byte[] getBufferBytes(Buffer b) { return b.buffer; }

    public static Buffer getBuffer2(Packet packet) {
        return packet.getBuffer();
    }

    public static void writeToOutputStream(Buffer buffer, OutputStream out1) throws IOException {
        int offset = getBufferS(buffer);
        int index = getBufferIndex(buffer);
        out1.write(getBufferBytes(buffer),offset,index);
    }

}
