package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import sun.awt.AppContext

import java.awt.Window
import java.lang.reflect.Field
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class JrrUtilities3Swing {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    // this field is null for openjdk 6
    public static Object rootTableKey;
    public static Map<ThreadGroup, AppContext> threadGroup2appContext;


    static {
        try {
            final Field threadGroup2appContextField = JrrClassUtils.findField(AppContext,"threadGroup2appContext");
            threadGroup2appContextField.setAccessible(true);
            threadGroup2appContext = (Map) threadGroup2appContextField.get(null);
            rootTableKey = JrrClassUtils.getFieldValue(new ClRef("javax.swing.SystemEventQueueUtilities"), "rootTableKey");
        } catch (final Exception e) {
            // TODO correctly catch headless exception
            log.log(Level.FINE, null, e);
        }

    }

    public static Collection<Window> findVisibleAwtWindows() {
        final Collection windowList = findAwtWindows();
        final Collection<Window> result = new HashSet();
        if (windowList == null) {
            log.info("can't find windows");
            return result;
        }
        for (final Object object : windowList) {
            if (object instanceof Window) {
                final Window window = (Window) object;
                if (window.isVisible()) {
                    result.add(window);
                }
            }
        }
        return result;
    }

    public static Collection<Window> findAwtWindows() {
        if (rootTableKey == null) {
            log.fine("rootTableKey is null");
            return Arrays.asList(Window.getWindows());
        }
        HashSet<Window> windows = new HashSet<Window>();
        Collection<AppContext> appContexts = threadGroup2appContext.values();
        for (AppContext context : appContexts) {

            final Map awtComponentsMap = (Map) context.get(rootTableKey);
            if (awtComponentsMap == null) {
                log.info("awp map is null");
            } else {
                final Collection<Window> windowList = (Collection)awtComponentsMap.keySet();
                windows.addAll(windowList);
            }
        }
        return windows;
    }


}
