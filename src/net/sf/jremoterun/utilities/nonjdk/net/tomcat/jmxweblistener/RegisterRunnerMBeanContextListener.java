package net.sf.jremoterun.utilities.nonjdk.net.tomcat.jmxweblistener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.SimpleFindParentClassLoader;


public class RegisterRunnerMBeanContextListener implements ServletContextListener {

    private static final Logger log = Logger
            .getLogger(RegisterRunnerMBeanContextListener.class.getName());

    private static Method webAppNameMethod;

    private String webAppName;

    static {
        // For some web containers there is no getContextPath method in
        // ServletContext class
        try {
            webAppNameMethod = ServletContext.class.getMethod("getContextPath");
        } catch (final NoSuchMethodException e) {
            log.severe("No getContextPath method");
            try {
                webAppNameMethod = ServletContext.class
                        .getMethod("getServletContextName");
            } catch (final Exception e1) {
                throw new Error(e1);
            }
        }
    }

    public static String getWebAppName(final ServletContext servletContext)
            throws Exception {
        final String name = (String) webAppNameMethod.invoke(servletContext);
        if ((name == null) || "".equals(name)) {
            return "/";
        }
        return name;
    }

    public void contextInitialized(final ServletContextEvent arg0) {
        final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        SimpleFindParentClassLoader.setDefaultClassLoader(classLoader);
        final MBeanServer mbeanServer = JrrUtils.findLocalMBeanServer();
        try {
            JrrUtils.registerJRemoteRunMBeans(mbeanServer);
            webAppName = getWebAppName(arg0.getServletContext());
            final WeakReference<ClassLoader> weakReference = new WeakReference<ClassLoader>(
                    classLoader);
            SharedObjectsUtils.getClassLoaders().put(webAppName, weakReference);
        } catch (final Exception e) {
            log.log(Level.WARNING, "contextInitialized", e);
            // throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(final ServletContextEvent arg0) {
    }
}