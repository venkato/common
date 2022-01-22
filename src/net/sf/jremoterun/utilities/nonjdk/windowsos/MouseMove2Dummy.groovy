package net.sf.jremoterun.utilities.nonjdk.windowsos

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.swing.JrrUtilities3Swing;

import java.util.logging.Logger;

@CompileStatic
class MouseMove2Dummy implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean increase = true;

    public volatile long sleepTime = 249_561 * 3

    public boolean doLogging = true;
    public volatile Thread thread;
    public volatile boolean doRun = true;
    public volatile Object lock = new Object();

    void doMove() {
        User32 b = User32.INSTANCE;
        WinDef.POINT p = new WinDef.POINT()
        boolean res = b.GetCursorPos(p);
        if (res) {
            doMoveImpl(p.x, p.y)
        } else {
            if (doLogging) {
                log.info "failed get cur ${new Date()}"
            }
        }
    }

    void doMoveImpl(int x, int y) {
        User32 b = User32.INSTANCE;
        if (increase) {
            x++
            y++
        } else {
            x--
            y--
        }
        increase = !increase
        boolean res = b.SetCursorPos(x, y)
        if (res) {
            if (doLogging) {
                log.info "move done ${x} ${y}"
            }
        } else {
            if (doLogging) {
                log.info "failed move ${x} ${y}"
            }
        }
    }


    void start() {
        thread = new Thread(this, 'Cursor move')
        thread.start()
    }

    void run() {
        runImpl()
    }

    void runImpl() {
        if (doLogging) {
            log.info "starting"
        }
        while (doRun) {
            try {
                doMove()
            }catch (Exception e){
                onException(e)
            }
            synchronized (lock) {
                lock.wait(sleepTime)
            }
        }
        if (doLogging) {
            log.info "finished"
        }
    }

    void onException(Exception e){
        log.severe("failed",e)
        JrrUtilitiesShowE.showException("failed",e);
    }

}
