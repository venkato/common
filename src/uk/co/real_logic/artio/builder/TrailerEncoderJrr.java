package uk.co.real_logic.artio.builder;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import java.util.logging.Logger;

@CompileStatic
public class TrailerEncoderJrr extends TrailerEncoder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    public int startTrailer(MutableAsciiBuffer buffer, int offset) {
        return super.startTrailer(buffer, offset);
    }

    @Override
    public long finishMessage(MutableAsciiBuffer buffer, int messageStart, int offset) {
        return super.finishMessage(buffer, messageStart, offset);
    }
}
