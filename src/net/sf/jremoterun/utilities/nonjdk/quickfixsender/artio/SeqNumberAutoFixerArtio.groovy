package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.quickfixsender.JrrQfHelper
import quickfix.Message
import quickfix.field.Text

import java.util.logging.Logger
import java.util.regex.Matcher

@CompileStatic
class SeqNumberAutoFixerArtio {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    boolean onLogoutWithText(String textLogout, boolean isOutMsg, JrrArtioDataHolder jrrQfHelper) {
        Matcher matcher = jrrQfHelper.d.seqNumMismatch.matcher(textLogout)
        if (matcher.matches()) {
            int expected = matcher.group(1) as int
            int my = matcher.group(2) as int;
            //jrrQfHelper.session.fixSeqNum(expected, my, isOutMsg)
            jrrQfHelper.t.jrrQfHelper.fixSeqNum(expected, my, isOutMsg)
            return true
        }
        return false
    }


}
