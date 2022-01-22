package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic
import io.aeron.logbuffer.ControlledFragmentHandler;
import net.sf.jremoterun.utilities.JrrClassUtils
import quickfix.field.Text
import uk.co.real_logic.artio.ValidationError
import uk.co.real_logic.artio.fields.AsciiFieldFlyweight
import uk.co.real_logic.artio.otf.MessageControl
import uk.co.real_logic.artio.otf.OtfMessageAcceptor
import uk.co.real_logic.artio.otf.OtfParser
import uk.co.real_logic.artio.util.AsciiBuffer

import java.awt.Color;
import java.util.logging.Logger;

@CompileStatic
class ArtioMsgParser implements OtfMessageAcceptor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrArtioDataHolder d;

    public boolean rejectMsg = false

    ArtioMsgParser(JrrArtioDataHolder d) {
        this.d = d
    }

    @Override
    MessageControl onNext() {
        rejectMsg = false
        return MessageControl.CONTINUE;
    }

    @Override
    MessageControl onComplete() {
        rejectMsg = false
        return MessageControl.CONTINUE;
    }

    @Override
    MessageControl onField(int tag, AsciiBuffer buffer, int offset, int length) {
        if (tag == quickfix.field.MsgType.FIELD) {
            //buffer.getChar(offset)
            String ascii = buffer.getAscii(offset, length)
            if (ascii == quickfix.field.MsgType.LOGOUT) {
                rejectMsg = true
                log.info "got logout msg"
            }
        }
        if (rejectMsg) {
            if (tag == Text.FIELD) {
                String ascii = buffer.getAscii(offset, length)
                if (d.fixMsgAutoFixer != null) {
                    d.fixMsgAutoFixer.onLogoutWithText(ascii, false, d);
                }
            }
        }
        return MessageControl.CONTINUE;
    }

    @Override
    MessageControl onGroupHeader(int tag, int numInGroup) {
        return MessageControl.CONTINUE;
    }

    @Override
    MessageControl onGroupBegin(int tag, int numInGroup, int index) {
        return MessageControl.CONTINUE;
    }

    @Override
    MessageControl onGroupEnd(int tag, int numInGroup, int index) {
        return MessageControl.CONTINUE;
    }

    @Override
    boolean onError(ValidationError error, long messageType, int tagNumber, AsciiFieldFlyweight value) {
        rejectMsg = false
        d.t.statusField.setText("error");
        d.t.statusField.setForeground(Color.YELLOW);
        d.t.addMessageToLogViewerCurrentDate("session error: " + error + " messageType: " + messageType + " tagNumber: " + tagNumber + " value: " + value);
        d.t.showLogs();
        return false;
    }
}
