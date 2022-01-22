package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import quickfix.Message
import quickfix.field.Text;

import java.util.logging.Logger
import java.util.regex.Matcher;

@CompileStatic
class SeqNumberAutoFixer {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    boolean onLogoutWithText(Message msg, boolean isOutMsg,JrrQfHelper jrrQfHelper) {
        String textLogout = msg.getString(Text.FIELD)
        Matcher matcher = jrrQfHelper.d.seqNumMismatch.matcher(textLogout)
        if (matcher.matches()) {
            int expected = matcher.group(1) as int
            int my = matcher.group(2) as int;
            jrrQfHelper.fixSeqNum(expected, my, isOutMsg)
            return true
        }
        return false
    }


}
