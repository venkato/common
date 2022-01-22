package net.sf.jremoterun.utilities.nonjdk.file

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.BytesToHumanConverter;

import java.util.logging.Logger;

@CompileStatic
class FileLengthCheck {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public BytesToHumanConverter bytesToHumanConverter = new BytesToHumanConverter()

    public static long hundred = 100

    void checkFreeSpace(File file, long minInBytes) {
        long length1 = file.getFreeSpace()
        if (length1 < minInBytes) {
            String msg = buildSuffix(file, length1, minInBytes, lowFreeSpace)
            onException(msg)
        }
    }

    void checkFileLength(File file, long maxInBytes) {
        if(!file.exists()){
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        long length1 = file.length()
        if (length1 > maxInBytes) {
            String msg = buildSuffix(file, length1, maxInBytes, fileTooBig)
            onException(msg)
        }
    }

    public String fileTooBig = "file too big = %s , max = %s"
    public String lowFreeSpace = "low free space %s , needed = %s"

    String buildSuffix(File f, long length1, long maxInBytes, String template) {
        return f.getAbsolutePathUnix() + " " + buildSuffix2(length1, maxInBytes, template)
    }

    public long maxDiffToLog = 4

    String buildSuffix2(long length1, long maxInBytes, String template) {
        String humanNow = bytesToHumanConverter.convertHuman(length1)
        String humanMax = bytesToHumanConverter.convertHuman(maxInBytes)

        String msg = String.format(template, humanNow, humanMax)
        long diff = length1 - maxInBytes
        if (maxInBytes > 0) {
            long percentage = (diff * hundred).intdiv(maxInBytes)
            if (percentage < maxDiffToLog) {
                String diffhuman = bytesToHumanConverter.convertHuman(diff)
                msg += " , diff = ${diffhuman}"
            }
        }
        return msg
    }

    void onException(String msg) {
        throw new Exception(msg)
    }


}
