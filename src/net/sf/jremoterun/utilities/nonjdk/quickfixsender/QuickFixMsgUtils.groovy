package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern;

@CompileStatic
class QuickFixMsgUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String msg;
//    public String humanSep;
    public char humanSep = '|';
    public Pattern msgLengthPattern = Pattern.compile('\\|9=\\d+\\|')
    public static char fixMsgNativeSep = '\u0001';

    void updateMsgLengthTag9() {
        int i = msg.indexOf('' + humanSep + '10=')
        if (i > 0) {
            msg = msg.substring(0, i)
        }
        if (msg.endsWith('' + humanSep)) {
            msg = msg.substring(0, msg.length() - 1)
        }

        int j = msg.indexOf('' + humanSep + '35=')
        if (j < 0) {
            throw new Exception("tag 35 not found : ${msg}")
        }
        String msgForCheckSum = msg.substring(j)

        Matcher matcher = msgLengthPattern.matcher(msg)
        if (matcher.find()) {
            msg = matcher.replaceFirst(''+humanSep + '9=' + msgForCheckSum.length() +''+ humanSep)
        } else {
            if (msg.startsWith('8=')) {
                throw new Exception("9 tag not found")
            }
            msg = '9=' + msgForCheckSum.length() + humanSep + msg
        }

    }

    void appendTag8IfNeeded(String beginString) {
        if (!msg.startsWith('8=')) {
            String msg2 = '8=' + beginString
//            String msg2 = '8=' + d.sessionID.getBeginString()
            if (msg.charAt(0) == humanSep) {
                msg = msg2 + msg
            } else {
                msg = msg2 + humanSep + msg
            }
        }
    }

    void addCheckSum() {
        int checkSum = buildCheckSum(msg)
        DecimalFormat decimalFormat = new DecimalFormat('000', DecimalFormatSymbols.getInstance(Locale.UK))
        String checkSumS = decimalFormat.format(checkSum)
        msg += '' + fixMsgNativeSep + '10=' + checkSumS + fixMsgNativeSep
    }


    int buildCheckSum(String msg) {
        int length = msg.length()
        int checkSum = 0;
        for (int i = 0; i < length; i++) {
            char charAt = msg.charAt(i)
            checkSum += charAt
        }
        checkSum = checkSum + 1
        checkSum = checkSum % 256
        return checkSum
    }


}
