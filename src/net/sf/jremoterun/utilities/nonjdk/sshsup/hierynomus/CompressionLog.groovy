package net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus

import groovy.transform.CompileStatic
import net.schmizz.sshj.common.Buffer
import net.schmizz.sshj.common.Message
import net.schmizz.sshj.transport.TransportException
import net.schmizz.sshj.transport.TransportImpl
import net.schmizz.sshj.transport.compression.Compression;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class CompressionLog implements Compression{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public TransportImpl transport;
    public Object encoder

    CompressionLog(TransportImpl transport) {
        this.transport = transport
        encoder = JrrClassUtils.getFieldValue(transport,'encoder');
        assert encoder!=null
    }

    void selfSet(){
//        JrrClassUtils.setFieldValueR(new ClRef('net.schmizz.sshj.transport.Converter'), encoder,'compression',this)
        JrrClassUtils.setFieldValueR(new ClRef('net.schmizz.sshj.transport.Encoder'), encoder,'compression',this)
    }

    @Override
    void init(Mode mode) {

    }

    @Override
    boolean isDelayed() {
        return false
    }

    public int shortSize = 100

    String shorter(String s){
        String shortM
        if(s.length()>shortSize){
            shortM= s.substring(0,shortSize)
        }else{
            shortM = s
        }
        return shortM
    }



    @Override
    void compress(Buffer buffer) {
        String string = new String(buffer.array(),buffer.rpos(),buffer.available())
        String shortM = shorter(string)
        byte b = 0
        if(buffer.array().length>5) {
            b = buffer.array()[5]
        }

        Message fromByte = Message.fromByte(b)
        String publickeyS = 'publickey'
        String passwordS = 'password'
        int i = string.indexOf(publickeyS)
        if (i > 0) {
            onPublicKey(fromByte,buffer, string)
        }else {
            if (string.contains(passwordS) && string.contains('ssh-connection')) {
                onPassword(fromByte,buffer, string)
            } else {
                //log.info("sending .. ${fromByte} ${b} bytes ${string.size()}  short = ${shortM}")
                logOutgoingMsg(string,b,fromByte,buffer)
            }
        }
    }

    void logOutgoingMsg(String string,byte b ,Message fromByte,Buffer buffer ){
        if(string.length()==0){
            log.info("sending .. ${fromByte} ${b} empty")
        }else {
            log.info("sending .. ${fromByte} ${b} : ${string.replace('\r',' ')}")
        }
    }

    private static final String digistsHex = "0123456789ABCDEF";

    static String getHex(byte[] raw,int offset,int length) {
        final StringBuilder hex = new StringBuilder(2 * length*2);
        for(int i=0;i<length;i++){
            byte b = raw[i+offset]
            hex.append(digistsHex.charAt((b & 0xF0) >> 4)).append(digistsHex.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public List<String> publicKeyShort = []

    public int pubkecKeyExtraLog = 40

    void onPublicKey(Message fromByte,Buffer buffer, String string) {
        String publickeyS = 'publickey'
        int i = string.indexOf(publickeyS)

        int maxlength = i + publickeyS.length() + pubkecKeyExtraLog
        String aa;
        if (string.length() > maxlength) {
            aa = string.substring(0, maxlength)
        } else {
            aa = string
        }
        publicKeyShort.add(aa)
        log.info("sending publickey .. ${fromByte} : ${aa} ... \n")
    }

    public boolean logPassword = false

    void onPassword(Message fromByte,Buffer buffer, String string) {
        if (logPassword) {
            log.info("sending .. : ${string}")
        } else {
            String publickeyS = 'password'
            int i = string.indexOf(publickeyS)

            int maxlength = i + publickeyS.length()
            String aa;
            aa = string.substring(0, maxlength)
            log.info ("sending .. ${fromByte} : ${aa} password_masked")
        }
    }

    @Override
    void uncompress(Buffer from, Buffer to) throws TransportException {

    }
}
