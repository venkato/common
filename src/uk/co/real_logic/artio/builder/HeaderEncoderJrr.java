package uk.co.real_logic.artio.builder;


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import java.util.logging.Logger;


@CompileStatic
public class HeaderEncoderJrr extends HeaderEncoder{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    public long startMessage(MutableAsciiBuffer buffer, int offset) {
        return super.startMessage(buffer, offset);
    }

    @Override
    public int finishHeader(MutableAsciiBuffer buffer, int bodyStart, int bodyLength) {
        return super.finishHeader(buffer, bodyStart, bodyLength);
    }
}
