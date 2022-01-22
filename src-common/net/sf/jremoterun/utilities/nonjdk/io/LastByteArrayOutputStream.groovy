package net.sf.jremoterun.utilities.nonjdk.io

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class LastByteArrayOutputStream extends ByteArrayOutputStream {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public static int msgSizeDefault = 240;
    /**
     * Value could be 8192 or higer
     * @see org.codehaus.groovy.runtime.ProcessGroovyMethods.ByteDumper
     */
    public static int bufferSizeDefault = 16_000;

//    int msgSize ;

    byte[] buf1;
    byte[] buf2;
    private final int lengthHalf;
    public volatile long totalWrittenBytes = 0
    public volatile Runnable runnableOnTooManyBytes;
    public volatile long tooManyBytesCount = Long.MAX_VALUE;

    volatile boolean usesFirst = true
    int countOldBuf = -1;

    LastByteArrayOutputStream() {
        this(bufferSizeDefault)
    }

    LastByteArrayOutputStream(int bufferSize) {
        super(0)
//        this.msgSize=msgSize;
        buf1 = new byte[bufferSize]
        buf2 = new byte[bufferSize]
        buf = buf1
        lengthHalf = bufferSize / 2 as int;
    }

//    void check() {
//        if (count > msgSize) {
//            checkImpl(false)
//        }
//        if (buf.is(buf1) || buf.is(buf2)) {
//        } else {
//            int len = Math.min(count, 80)
//            String s = new String(buf, 0, len)
//            log.info "got another ref : ${count} : ${s}"
//            checkImpl(true)
//        }
//    }

    void ensureBytesFree(int byteFreeNeeded) {
        if (byteFreeNeeded > buf1.length) {
            throw new IllegalStateException("needed=${byteFreeNeeded}, configured=${buf1.length} ")
        }
        int freeCount = buf1.length - count
        if (freeCount < byteFreeNeeded) {
            swap()
        }
    }

    synchronized void swap() {
        byte[] freeBuf = usesFirst ? buf2 : buf1
//        System.arraycopy(buf, begin, freeBuf, 0, msgSize)
        countOldBuf = count;
        count = 0
        buf = freeBuf
        usesFirst = !usesFirst
    }

//    synchronized void checkImpl(boolean anotherRef) {
//        int begin = count - msgSize
//        if (begin < 0) {
//            if (anotherRef) {
//
//            } else {
//                String s = new String(buf, 0, count)
//                log.warn "${count} , ${s}"
//                throw new IllegalStateException("${begin} ${count}")
//            }
//        }
//        swap()
//    }

    @Override
    synchronized void write(int b) {
        //check()
        ensureBytesFree(2)
        super.write(b)
        totalWrittenBytes ++
        checkTooManyBytes()
//        check()
    }

    @Override
    synchronized void write(byte[] b) throws IOException {
        //check()
        //ensureBytesFree(b.length+1)
        super.write(b)
        //check()
    }

    @Override
    synchronized void write(byte[] b, int off, int len) {
        //check()
        if (len > buf1.length) {
            int reduceCount = len - buf1.length + 1
            off += reduceCount
            len = len - reduceCount
        }
        ensureBytesFree(len)
        super.write(b, off, len)
        totalWrittenBytes += len
        //check()
        checkTooManyBytes()
    }

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
