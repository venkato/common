package net.sf.jremoterun.utilities.nonjdk.net.tomcat.jmxweblistener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.RemoteRunner;
import net.sf.jremoterun.RemoteRunnerWithCodeUploading;
import net.sf.jremoterun.SimpleFindParentClassLoader;
import net.sf.jremoterun.mbeans.Runner;

public class RunJMXServerContextListener implements ServletContextListener {

    private static final Logger log = Logger
            .getLogger(RunJMXServerContextListener.class.getName());

    public static String jmxPortS = "portJmx";
    public static String nameOfPawordS = "usePawordJmx";
    public static String pawordS = "pawordJmx";
    public static int rmiRegistryPort;

    public static String use = null;

    private static String paword = null;

    private static RMIConnectorServer server;

    public void contextInitialized(final ServletContextEvent arg0) {
        String jmxPort2 = arg0.getServletContext().getInitParameter(jmxPortS);
        if (jmxPort2 == null) {
            log.info(jmxPortS + " param not found");
        } else {
            rmiRegistryPort = Integer.parseInt(jmxPort2.trim());
            use = arg0.getServletContext().getInitParameter(nameOfPawordS);
            paword = arg0.getServletContext().getInitParameter(pawordS);
            final ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();
            SimpleFindParentClassLoader.setDefaultClassLoader(classLoader);
            if (server == null || !server.isActive()) {
                try {
                    server = JrrUtils.creatJMXConnectorAndRMIRegistry(JrrUtils.findLocalMBeanServer(), rmiRegistryPort, use, paword);
                    log.fine("Staring JMX connector on " + rmiRegistryPort + " port.");
                    JrrUtils.registerJRemoteRunMBeans(server.getMBeanServer());
                } catch (final Exception e) {
                    log.log(Level.WARNING, "contextInitialized", e);
                }
            }
        }
    }

    public void contextDestroyed(final ServletContextEvent arg0) {
        final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == SimpleFindParentClassLoader.getDefaultClassLoader()) {
            if (classLoader == Runner.class.getClassLoader()) {
                log.warning("Incorrect default class loader");
            } else {
                SimpleFindParentClassLoader.setDefaultClassLoader(Runner.class
                        .getClassLoader());
            }
        }
        if (server == null) {
            log.warning("rmi server is null");
        } else {
            if (server.isActive()) {
                try {
                    final MBeanServer mbeanServer = server.getMBeanServer();
                    server.stop();
                    MBeanServerFactory.releaseMBeanServer(mbeanServer);
                    JrrUtils.unregisterMBeanQuiet(mbeanServer,
                            RemoteRunner.runner);
                    JrrUtils.unregisterMBeanQuiet(mbeanServer,
                            RemoteRunnerWithCodeUploading.dataUploader);
                } catch (final Exception e) {
                    log.log(Level.WARNING, "web context destroyed", e);
                }
            }
        }
    }
}