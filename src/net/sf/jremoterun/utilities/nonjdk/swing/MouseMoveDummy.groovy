package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
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
    public Date lastDisplayedFailedPointer
    public long ignoreFailedPointerDuration = DurationConstants.oneHour.timeInMsLong * 3


    MouseMoveDummy(int periodInSec) {
        adjustPeriodTimer = new AdjustPeriodTimer(periodInSec * 1000, TimerStyle.Consecutive, {
            doMove();
        })
    }

    void start() {
        adjustPeriodTimer.start2()
    }


    void doMove() {
        PointerInfo pionter1 = MouseInfo.getPointerInfo();
        if (pionter1 == null) {
            onNullPointer()
        } else {
            onGoodPointer(pionter1)
        }
    }

    void onGoodPointer(PointerInfo pionter1) {
        lastDisplayedFailedPointer = null
        Point b = pionter1.getLocation();
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
        consequentFailed = 0
    }


    void onNullPointer() {
        log.info "pointer is null"
        consequentFailed++
        if (consequentFailed > consequentFailedMax) {
            boolean showError1 = true
            if (lastDisplayedFailedPointer != null) {
                long diff = System.currentTimeMillis() - lastDisplayedFailedPointer.getTime()
                if (diff < ignoreFailedPointerDuration) {
                    showError1 = false
                }
            }
            if (showError1) {
                showError()
                lastDisplayedFailedPointer = new Date()
            }
        }
    }

    void showError() {
        JrrUtilitiesShowE.showException("failed get mouse pointer", new JustStackTrace())
    }

}
