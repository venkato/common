package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio;


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import uk.co.real_logic.artio.engine.EngineConfiguration;
import uk.co.real_logic.artio.engine.framer.DefaultTcpChannelSupplier;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;


@CompileStatic
public class DefaultTcpChannelSupplierJrr extends DefaultTcpChannelSupplier {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public DefaultTcpChannelSupplierJrr(EngineConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void open(InetSocketAddress address, InitiatedChannelHandler channelHandler) throws IOException {
        log.info("open channel : " + address);
        super.open(address, channelHandler);
    }
}
