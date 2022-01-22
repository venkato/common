package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate;

import java.util.logging.Logger;

@CompileStatic
class StoreDataFile {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File f;
    public int rotateCount;
    public boolean needStore = false;

    StoreDataFile(File f, int rotateCount) {
        this.f = f
        this.rotateCount = rotateCount
    }


    void store(String human1){
        storeB(convertToBytes(human1))
    }

    byte[] convertToBytes(String s ){
        return s.getBytes();
    }

    void isNeedStore(byte[] newB){
        if (!f.exists()) {
            needStore = true
        }
        if (!needStore) {
            needStore = newB.length != f.length()
        }
        if (!needStore) {
            needStore = isContentSame(newB)
        }
    }

    boolean isContentSame(byte[] newB){
        byte[] bytes1 = f.bytes
        return !isEquals(newB,bytes1)
    }

    void storeB(byte[] newB){
        needStore = false
        isNeedStore(newB)
        if (needStore) {
            doStore(newB)
        }
    }

    void doStore(byte[] newB){
        FileRotate.rotateFile(f, rotateCount)
        f.bytes = newB
    }

    boolean isEquals(byte[] newB,byte[] existedB){
        return Arrays.equals(newB, existedB)
//        return newB == existedB
    }
}
