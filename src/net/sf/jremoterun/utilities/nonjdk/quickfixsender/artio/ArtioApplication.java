/*
 * Copyright 2015-2025 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio;

import io.aeron.logbuffer.ControlledFragmentHandler.Action;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.quickfixsender.QuickFixMsgUtils;
import org.agrona.DirectBuffer;
import quickfix.InvalidMessage;
import quickfix.Message;
import uk.co.real_logic.artio.dictionary.LongDictionary;
import uk.co.real_logic.artio.library.OnMessageInfo;
import uk.co.real_logic.artio.library.SessionHandler;
import uk.co.real_logic.artio.messages.DisconnectReason;
import uk.co.real_logic.artio.otf.OtfParser;
import uk.co.real_logic.artio.session.Session;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import io.aeron.logbuffer.ControlledFragmentHandler;

public class ArtioApplication implements SessionHandler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public JrrArtioDataHolder d;

    public ArtioApplication(JrrArtioDataHolder d) {
        this.d = d;
        artioMsgParser = new ArtioMsgParser(d);
        parser = new OtfParser(artioMsgParser, new LongDictionary());
    }

    public ArtioMsgParser artioMsgParser;

    private OtfParser parser;

    //private String testReqId;

    public Action onMessage(
            final DirectBuffer buffer,
            final int offset,
            final int length,
            final int libraryId,
            final Session session,
            final int sequenceIndex,
            final long messageType,
            final long timestampInNs,
            final long position,
            final OnMessageInfo messageInfo) {
        byte[] message = new byte[length];
        buffer.getBytes(offset, message);
        String msgStrShos = new String(message, StandardCharsets.US_ASCII);
        String msgStr = msgStrShos.replace(QuickFixMsgUtils.fixMsgNativeSep, d.humanSep);
        log.info("Incoming FIX: " + msgStr);
        d.latestIncomingMsgShos = msgStrShos;
        d.latestIncomingMsg.putBytes(0, buffer, offset, length);
        d.latestTestRequestMessageLength = length;
//        testReqId = null;
        parser.onMessage(buffer, offset, length);

//        if (testReqId != null)
//        {
//        }
        try {
            d.t.addMessageToView(new Message(msgStrShos, false), false);
        } catch (InvalidMessage e) {
            log.log(java.util.logging.Level.SEVERE, "" + e + " msg: " + msgStr, e);
            d.t.addMessageToLogViewerCurrentDate("Error parsing message: " + e.getMessage() + " msg: " + msgStr);
            d.t.showLogs();
        }
        return ControlledFragmentHandler.Action.CONTINUE;
    }

    public void onTimeout(final int libraryId, final Session session) {
        d.t.statusField.setText("session timeout");
        d.t.statusField.setForeground(Color.YELLOW);
        d.t.addMessageToLogViewerCurrentDate("session timeout");
        d.t.showLogs();
    }

    public void onSlowStatus(final int libraryId, final Session session, final boolean hasBecomeSlow) {
        d.t.statusField.setText("slow: " + hasBecomeSlow);
        d.t.statusField.setForeground(Color.YELLOW);
        d.t.addMessageToLogViewerCurrentDate("session slow: " + hasBecomeSlow);
        d.t.showLogs();
    }

    public Action onDisconnect(final int libraryId, final Session session, final DisconnectReason reason) {
        d.t.statusField.setText("disconnected: " + reason);
        d.t.statusField.setForeground(Color.RED);
        d.t.addMessageToLogViewerCurrentDate("session disconnected reason: " + reason);
        d.t.showLogs();
        return ControlledFragmentHandler.Action.CONTINUE;
    }

    public void onSessionStart(final Session session) {
        d.t.statusField.setText("started");
        d.t.statusField.setForeground(Color.YELLOW);
        d.t.addMessageToLogViewerCurrentDate("session started");
        d.t.showLogs();
    }


}
