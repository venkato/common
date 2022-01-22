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
    public static char humanSepDefault = '|';
    public char humanSep = humanSepDefault;
    public Pattern msgLengthPattern = Pattern.compile('\\|9=\\d+\\|')
    public static char fixMsgNativeSep = '\u0001';
    public static String fixMsgNativeSepStr = fixMsgNativeSep;

    QuickFixMsgUtils() {
    }

    String getTagValue(int tag) {
        String tagText = '' + tag + '='
        int tagStart = msg.indexOf(tagText)
        while (tagStart >= 0) {
            boolean atFieldStart = tagStart == 0 || isFieldSeparator(msg.charAt(tagStart - 1))
            if (atFieldStart) {
                int valueStart = tagStart + tagText.length()
                int valueEnd = valueStart
                while (valueEnd < msg.length() && !isFieldSeparator(msg.charAt(valueEnd))) {
                    valueEnd++
                }
                return msg.substring(valueStart, valueEnd)
            }
            tagStart = msg.indexOf(tagText, tagStart + tagText.length())
        }
        throw new FixTagNotFoundException("tag ${tag} not found in ${msg}")
    }

    boolean isTagSet(int tag) {
        String tagText = '' + tag + '='
        int tagStart = msg.indexOf(tagText)
        while (tagStart >= 0) {
            boolean atFieldStart = tagStart == 0 || isFieldSeparator(msg.charAt(tagStart - 1))
            if (atFieldStart) {
                int valueStart = tagStart + tagText.length()
                int valueEnd = valueStart
                while (valueEnd < msg.length() && !isFieldSeparator(msg.charAt(valueEnd))) {
                    valueEnd++
                }
                return true
            }
            tagStart = msg.indexOf(tagText, tagStart + tagText.length())
        }
        return false
    }


    void removeTag(int tag, boolean throwExceptionIfNotFound) {
        final String origMsg = msg
        String tagWIthEq = '' + tag + '='
        int tagStart = msg.indexOf(tagWIthEq)
        if (tagStart == -1) {
            if (throwExceptionIfNotFound) {
                throw new FixTagNotFoundException("tag ${tag} not found in ${msg}")
            } else {
            }
        } else {
            while (tagStart >= 0) {
                boolean atFieldStart = tagStart == 0 || isFieldSeparator(msg.charAt(tagStart - 1))
                if (atFieldStart) {
                    int tagEnd = tagStart + tagWIthEq.length()
                    while (tagEnd < msg.length() && !isFieldSeparator(msg.charAt(tagEnd))) {
                        tagEnd++
                    }

                    if (tagEnd < msg.length()) {
                        // Remove the field and its following separator.
                        msg = msg.substring(0, tagStart) + msg.substring(tagEnd + 1)
                    } else if (tagStart > 0) {
                        // For a final field, remove its preceding separator too.
                        msg = msg.substring(0, tagStart - 1)
                    } else {
                        msg = origMsg
                        throw new Exception("msg is empty ${origMsg}")
                    }
                    tagStart = msg.indexOf(tagWIthEq)
                } else {
                    tagStart = msg.indexOf(tagWIthEq, tagStart + tagWIthEq.length())
                }
            }
        }
    }

    void prepareForArtio() {
        msg = msg.replace(humanSep, fixMsgNativeSep)
        if (msg.startsWith(fixMsgNativeSepStr)) {
            msg = msg.substring(1)
        }
        if (!msg.endsWith(fixMsgNativeSepStr)) {
            msg += fixMsgNativeSepStr
        }
    }

    void replaceTagValue(int tag, String value, boolean throwExceptionIfNotFound) {
        String origMsg = msg
        String tagText = '' + tag + '='
        int searchFrom = 0
        int tagStart = msg.indexOf(tagText, searchFrom)
        if (tagStart == -1) {
            if (throwExceptionIfNotFound) {
                throw new FixTagNotFoundException("tag ${tag} not found in ${msg}")
            }
        } else {
            while (tagStart >= 0) {
                boolean atFieldStart = tagStart == 0 || isFieldSeparator(msg.charAt(tagStart - 1))
                if (atFieldStart) {
                    int valueStart = tagStart + tagText.length()
                    int valueEnd = valueStart
                    while (valueEnd < msg.length() && !isFieldSeparator(msg.charAt(valueEnd))) {
                        valueEnd++
                    }
                    msg = msg.substring(0, valueStart) + value + msg.substring(valueEnd)
                    searchFrom = valueStart + value.length()
                    tagStart = msg.indexOf(tagText, searchFrom)
                } else {
                    tagStart = msg.indexOf(tagText, tagStart + tagText.length())
                }
            }
        }
    }

    boolean isFieldSeparator(char value) {
        return value == humanSep || value == fixMsgNativeSep
    }

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
            msg = matcher.replaceFirst('' + humanSep + '9=' + msgForCheckSum.length() + '' + humanSep)
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
