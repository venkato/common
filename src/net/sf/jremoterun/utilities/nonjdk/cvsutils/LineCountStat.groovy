package net.sf.jremoterun.utilities.nonjdk.cvsutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LineCountStat {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public long logEvery = 100_000_000
    public long[] less1 = []
    public long[] devide1 = []
    public boolean[] used1 = []
    public int countEvery = 1
    public int countElsInArray = 16
    public long logFirst =3
    public BigInteger maxCheck = BigInteger.valueOf(Long.MAX_VALUE/10/2 as long)

    static BigInteger ten=BigInteger.valueOf(10)

    LineCountStat() {
        fillArray(countElsInArray)
    }



    void fillArray(int count1) {
        less1 = new long[count1]
        devide1 = new long[count1]
        used1 = new boolean[count1]
        for (int i = 1; i < count1; i+=countEvery) {
            used1[i] = false

            if(ten>maxCheck){
                log.fine("skip as too big ${i}")
                count1 = i-countEvery;
            }else {
                devide1[i] = ten.pow(i).longValue()
                less1[i] = logFirst * devide1[i];
            }
        }
        log.fine "devide1 = ${devide1}"
        log.fine "less1 = ${less1}"
    }


    boolean isNeedPrintProgress2(long linesRead, int i) {
        if (linesRead < less1[i]) {
            long remidere = linesRead % devide1[i]
            if (remidere == 0) {
                return true
            }
        }
        return false
    }



    boolean isNeedPrintProgress(long linesRead) {
        if (linesRead < logFirst) {
            return true
        }
        for (int i = 1; i < countElsInArray; i+=countEvery) {
            boolean b = isNeedPrintProgress2(linesRead, i)
            if (b) {
                return true
            }
        }
        if (linesRead % logEvery == 0) {
            return true
        }
        return false
    }


}
