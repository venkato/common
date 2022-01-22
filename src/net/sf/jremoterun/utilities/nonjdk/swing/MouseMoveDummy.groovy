package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import junit.framework.TestCase;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.timer.AdjustPeriodTimer
import net.sf.jremoterun.utilities.nonjdk.timer.Timer
import net.sf.jremoterun.utilities.nonjdk.timer.TimerStyle

import java.awt.MouseInfo
import java.awt.Point
import java.awt.PointerInfo
import java.awt.Robot;
import java.util.logging.Logger;

@CompileStatic
class MouseMoveDummy {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Robot r = new Robot();
    public AdjustPeriodTimer adjustPeriodTimer

    MouseMoveDummy(int periodInSec ) {
        adjustPeriodTimer = new AdjustPeriodTimer(periodInSec*1000, TimerStyle.Consecutive, {
            doMove();
        })
    }

    void start(){
        adjustPeriodTimer.start2()
    }



    void doMove(){
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        int x = (int) b.getX();
        int y = (int) b.getY();
        r.mouseMove(x+1, y+1);
    }

}
