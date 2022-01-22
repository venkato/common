package net.sf.jremoterun.utilities.nonjdk.windowsos

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.timer.simple1.PeriodicTaskSimple;

import java.util.logging.Logger;

@CompileStatic
class MouseMove2Dummy extends PeriodicTaskSimple {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean increase = true;

    public boolean doLogging = true;

    MouseMove2Dummy() {
        super(249_561 * 3,'Cursor move')
        showExceptionInSwing = true
        stopOnException = false;
    }


    @Override
    void doJob() {
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



}
