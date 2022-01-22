package net.sf.jremoterun.utilities.nonjdk.cvsutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class LineCount2Stat {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private long logEvery = 100_000_000
    public long logEveryNext = logEvery
    public long[] less1 = []
//    public long[] devide1 = []
    public boolean[] used1 = []
    public int countEvery = 1
    public int countElsInArray = 16
    public long logFirst = 1
    public BigInteger maxCheck = BigInteger.valueOf(Long.MAX_VALUE / 10 / 2 as long)

    LineCount2Stat() {
        fillArray(countElsInArray)
    }

    long getLogEvery() {
        return logEvery
    }

    void setLogEvery(long logEvery) {
        this.logEvery = logEvery
        logEveryNext = logEvery
    }

    public static BigInteger ten = BigInteger.valueOf(10)

    void fillArray(int count1) {
        less1 = new long[count1]
        //less1 = new long[count1]
        used1 = new boolean[count1]
        for (int i = 1; i < count1; i += countEvery) {
            used1[i] = false

            if (ten > maxCheck) {
                log.fine("skip as too big ${i}")
                count1 = i - countEvery;
            } else {
                long devide11 = ten.pow(i).longValue()
                less1[i] = logFirst * devide11;
            }
        }
        //log.fine "devide1 = ${devide1}"
        log.fine "less1 = ${less1}"
    }


    boolean isNeedPrintProgress2(long linesRead, int i) {
        if (linesRead < less1[i]) {
            if(i==1){

            }else {
                assert i>1
                if( linesRead< less1[i-1]){
                    return false
                }
            }
            if (used1[i]) {
                return false
            }
            used1[i] = true
            return true
        }
        return false
    }


    void calcLogEveryNext(long linesRead) {

        long rem = linesRead % logEvery
        logEveryNext = linesRead - rem + logEvery
    }


    boolean isNeedPrintProgress(long linesRead) {
        if (linesRead < logFirst) {
            return true
        }
        boolean logEveryTriggered = false
        if (linesRead >= logEveryNext) {
            logEveryTriggered = true
            calcLogEveryNext(linesRead)
        }
        //log.info "${linesRead} ${logEveryTriggered}"
        for (int i = 1; i < countElsInArray; i += countEvery) {
            boolean b = isNeedPrintProgress2(linesRead, i)
            if (b) {
                return true
            }
        }

        return logEveryTriggered
    }


}
