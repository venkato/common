package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrJschSessionOriginal extends Session {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrJschSessionOriginal(JSch jsch, String username, String host, int port) throws JSchException {
        super(jsch, username, host, port)
    }

    @Override
    void write(Packet packet, Channel c, int length) throws Exception {
        super.write(packet, c, length)
    }

    @Override
    void addChannel(Channel channel) {
        super.addChannel(channel)
    }

    @Override
    void setUserName(String username) {
        super.setUserName(username)
    }

    @Override
    IdentityRepository getIdentityRepository() {
        return super.getIdentityRepository()
    }


    int getAuth_failuresC() {
        return auth_failures;
    }

    void setAuth_failuresC(int auth_failuresC) {
        this.auth_failures = auth_failuresC;
    }


    int getMax_auth_triesC() {
        return max_auth_tries;
    }

    void setMax_auth_triesC(int max_auth_triesC) {
        this.max_auth_tries = max_auth_triesC;
    }


//    static int getBufferIndex(Buffer b) { return b.index }
//
//    static int getBufferS(Buffer b) { return b.s }
//
//    static byte[] getBufferBytes(Buffer b) { return b.buffer }
//
//    static Buffer getBuffer2(Packet packet) {
//        return packet.getBuffer()
//    }
//    @Override
//    void encode(Packet packet) throws Exception {
//        super.encode(packet)
//    }

    @Override
    void encode(Packet packet) throws Exception {
        Buffer buffer = JschBufferHelper.getBuffer2(packet)
        onOutputBuffer(buffer);
        super.encode(packet)
    }

//    @Override
//    void write(Packet packet) throws Exception {
//        Buffer buffer = JschBufferHelper.getBuffer2(packet)
//                onOutputBuffer(buffer);
//        super.write(packet)
//    }

    void onOutputBuffer(Buffer buffer) {

    }


    @Override
    Buffer read(Buffer buf) throws Exception {
        return super.read(buf);
    }

//    @Override
//    String[] getServerSigAlgs(){
//        return super.getServerSigAlgs()
        //return serverSigAlgs;
//    }

//    String[] getGuess1(){
//        return guess
//    }

}
