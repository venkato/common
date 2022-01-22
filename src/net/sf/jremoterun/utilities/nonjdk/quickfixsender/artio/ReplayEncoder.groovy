package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import uk.co.real_logic.artio.builder.Encoder
import uk.co.real_logic.artio.builder.HeaderEncoderJrr
import uk.co.real_logic.artio.builder.SessionHeaderEncoder
import uk.co.real_logic.artio.builder.TrailerEncoderJrr
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import java.util.logging.Logger;

@CompileStatic
class ReplayEncoder implements Encoder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HeaderEncoderJrr header222 = new HeaderEncoderJrr();

    //String msg;
    public String msgSoh
    public byte[] msgType
    public long msgType2

    ReplayEncoder(String msgType, String msg) {
        msgSoh = msg
        this.msgType = msgType.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        assert msgType.length() < 5
        msgType2 = messageType2(this.msgType)
    }

    @Override
    public long encode(MutableAsciiBuffer buffer, int offset) {
        header222.msgType(msgType);
        long startMessageResult = header222.startMessage(buffer, offset);
        int bodyStart = Encoder.offset(startMessageResult);
        int position = bodyStart + Encoder.length(startMessageResult);
        buffer.putBytes(position, msgSoh.getBytes());
        position += msgSoh.getBytes().length;
        TrailerEncoderJrr trailer = new TrailerEncoderJrr();
        position += trailer.startTrailer(buffer, offset);
        int messageStart = header222.finishHeader(buffer, bodyStart, position - bodyStart);
        long lenth = trailer.finishMessage(buffer, messageStart, position);
        log.info("msg = " + new String(buffer.byteArray(), offset + 7, (int) lenth));
        return lenth;
    }

    @Override
    void reset() {

    }

    @Override
    long messageType() {
        return msgType2;
    }


    static long messageType3(String msgType3) {
        messageType2(msgType3.getBytes(java.nio.charset.StandardCharsets.US_ASCII))
    }

    static long messageType2(byte[] msgType3) {
        assert msgType3.length < 5: "Message type is too long";
        long result = 0;
        for (i in 0..<msgType3.length) {

            int shiftBy = 8 * i;
            long v = msgType3[i];
            v = (v << shiftBy)

            result += v;
        }
        return result
    }

    @Override
    SessionHeaderEncoder header() {
        return header222;
    }

    @Override
    void resetMessage() {

    }

    @Override
    Encoder copyTo(Encoder otherEncoder) {
        return null
    }

    @Override
    StringBuilder appendTo(StringBuilder builder) {
        return null
    }
}
