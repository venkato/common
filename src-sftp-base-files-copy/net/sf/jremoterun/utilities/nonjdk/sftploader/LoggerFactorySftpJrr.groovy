package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic
import net.schmizz.sshj.common.LoggerFactory;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LoggerFactorySftpJrr implements LoggerFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    org.slf4j.jul.JDK14LoggerFactory jdk14LoggerFactory =new org.slf4j.jul.JDK14LoggerFactory();

    @Override
    org.slf4j.Logger getLogger(String name) {
        return jdk14LoggerFactory.getLogger(name)
    }

    @Override
    org.slf4j.Logger getLogger(Class<?> clazz) {
        return jdk14LoggerFactory.getLogger(clazz.getName())
    }
}
