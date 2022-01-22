package net.sf.jremoterun.utilities.nonjdk.log.log4j2;

import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;

public class Log4j2LoggerContext implements LoggerContext {
    @Override
    public Object getExternalContext() {
        return null;
    }

    @Override
    public ExtendedLogger getLogger(String name) {
        return null;
    }

    @Override
    public ExtendedLogger getLogger(String name, MessageFactory messageFactory) {
        return null;
    }

    @Override
    public boolean hasLogger(String name) {
        return false;
    }

    @Override
    public boolean hasLogger(String name, Class<? extends MessageFactory> messageFactoryClass) {
        return false;
    }

    @Override
    public boolean hasLogger(String name, MessageFactory messageFactory) {
        return false;
    }
}
