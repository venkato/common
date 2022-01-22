package net.sf.jremoterun.utilities.nonjdk.io

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class LastCharArrayOutputStream  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public static int msgSizeDefault = 240;
    /**
     * Value could be 8192 or higer
     * @see org.codehaus.groovy.runtime.ProcessGroovyMethods.ByteDumper
     */
    public static int bufferSizeDefault = 16_000;

//    int msgSize ;

    public char[] buf;
    public int count;

    public char[] buf1;
    public char[] buf2;
    private int lengthHalf;
    public volatile long totalWrittenBytes = 0
    public volatile Runnable runnableOnTooManyBytes;
    public volatile long tooManyBytesCount = Long.MAX_VALUE;

    public volatile boolean usesFirst = true
    public int countOldBuf = -1;

    LastCharArrayOutputStream() {
        this(bufferSizeDefault)
    }

    LastCharArrayOutputStream(int bufferSize) {
//        this.msgSize=msgSize;
        setNewBufferSize(bufferSize)
    }

    void setNewBufferSize(int bufferSize){
        buf1 = new byte[bufferSize]
        buf2 = new byte[bufferSize]
        buf = buf1
        lengthHalf = bufferSize / 2 as int;
    }

    void ensureBytesFree(int byteFreeNeeded) {
        if (byteFreeNeeded > buf1.length) {
            throw new IllegalStateException("needed=${byteFreeNeeded}, configured=${buf1.length} ")
        }
        int freeCount = buf1.length - count
        if (freeCount < byteFreeNeeded) {
            swap()
        }
    }

    void onSwap(char[] c,int len){

    }

    synchronized void swap() {
        onSwap(buf,count)
        byte[] freeBuf = usesFirst ? buf2 : buf1
//        System.arraycopy(buf, begin, freeBuf, 0, msgSize)
        countOldBuf = count;
        count = 0
        buf = freeBuf
        usesFirst = !usesFirst
    }

//    @Override
    synchronized void write(char b) {
        //check()
        ensureBytesFree(2)
        buf[count] = (byte) b;
        count += 1;
        totalWrittenBytes++
        checkTooManyBytes()
    }

//    @Override
//    synchronized void write(byte[] b) throws IOException {
//        super.write(b)
//    }

//    @Override
//    synchronized void write(byte[] b, int off, int len) {
//        if (len > buf1.length) {
//            int reduceCount = len - buf1.length + 1
//            off += reduceCount
//            len = len - reduceCount
//        }
//        ensureBytesFree(len)
//        super.write(b, off, len)
//        totalWrittenBytes += len
//        //check()
//        checkTooManyBytes()
//    }

    void checkTooManyBytes(){
        if(runnableOnTooManyBytes!=null && totalWrittenBytes>tooManyBytesCount){
            runnableOnTooManyBytes.run()
        }
    }

    @Override
    String toString() {
        if ((count < (lengthHalf - 1)) && countOldBuf > 0) {
            String oldBufS
            byte[] oldBuf = usesFirst ? buf2 : buf1;
            oldBufS = new String(oldBuf, 0, countOldBuf)
            return oldBufS + new String(buf, 0, count)
        }
        return new String(buf, 0, count)
    }


}
