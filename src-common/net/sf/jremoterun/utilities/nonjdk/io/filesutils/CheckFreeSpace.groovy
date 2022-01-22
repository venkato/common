package net.sf.jremoterun.utilities.nonjdk.io.filesutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.byteshuman.BytesToHumanConverter
import net.sf.jremoterun.utilities.nonjdk.sshsup.BytesDivider;

import java.util.logging.Logger;

@CompileStatic
class CheckFreeSpace {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();




    static void checkDiskFreeSpace(File file, long minFreeSpaceInMb) throws IOException {
        checkDiskFreeSpaceInBytes(file, BytesDivider.mb.multBy( minFreeSpaceInMb ));
    }

    static void checkDiskFreeSpaceInBytes(File file, long minFreeSpaceInBytes) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        long freeSpace = file.getFreeSpace();
        if (freeSpace < minFreeSpaceInBytes) {
            String human = new BytesToHumanConverter().convertHuman(freeSpace)
            throw new IOException("low free space " + human + " in " + file.getAbsolutePath());
        }
    }

}
