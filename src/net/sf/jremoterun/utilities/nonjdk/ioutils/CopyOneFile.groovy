package net.sf.jremoterun.utilities.nonjdk.ioutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc;

import java.util.logging.Logger;

@CompileStatic
class CopyOneFile {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int rotateCount=0


     boolean copyIfDifferentS(File f1, File f2) {
        assert f1.exists()
        boolean needCopy = isNeedCopy(f1,f2);
        if(needCopy) {
            if(rotateCount>0){
                doRotate(f2)
            }
            doCopy(f1,f2)
        }
        return needCopy
    }

    void doRotate(File f){
        FileRotate.rotateFile(f,rotateCount)
    }

    void doCopy(File f1, File f2){
        FileUtilsJrr.copyFile(f1, f2)
    }

    boolean isNeedCopy(File f1, File f2){
        boolean needCopy;
        if(f2.exists()) {
            needCopy = isFileEquals(f1, f2)
        }else{
            needCopy=true
            File parentFile = f2.getParentFile()
            assert parentFile.exists()
        }
        return needCopy
    }

     boolean isFileEquals(File f1, File f2) {
         return isFileEqualsS(f1,f2)
     }

     static boolean isFileEqualsS(File f1, File f2) {
        if (!f2.exists()) {
            return false
        }
        assert f1.exists()
        if (f1.length() != f2.length()) {
            return false
        }
        String sha1 = FileCheckSumCalc.calcSha256ForFile(f1)
        String sha2 = FileCheckSumCalc.calcSha256ForFile(f2)
        return sha1 == sha2
    }


}
