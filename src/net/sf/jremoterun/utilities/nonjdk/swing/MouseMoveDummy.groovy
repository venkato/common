package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace
import net.sf.jremoterun.utilities.nonjdk.timer.AdjustPeriodTimer
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
    public volatile boolean increase = true

    public int consequentFailed = 0
    public int consequentFailedMax = 3

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
        if(a==null){
            log.info "pointer is null"
            consequentFailed++
            if(consequentFailed>consequentFailedMax){
                JrrUtilitiesShowE.showException("failed get mouse pointer",new JustStackTrace())
            }
        }else {
            Point b = a.getLocation();
            int x = (int) b.getX();
            int y = (int) b.getY();
            if (increase) {
                x++
                y++
            } else {
                x--
                y--
            }
            increase = !increase
            r.mouseMove(x, y);
            consequentFailed =0
        }
    }

}
