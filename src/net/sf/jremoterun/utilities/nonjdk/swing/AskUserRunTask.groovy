package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.ObjectWrapper
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea

import javax.swing.*
import java.awt.*
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
abstract class AskUserRunTask {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //public String taskGroupName
    public AskUserRunTaskAux askUserRunTaskAuxLatest;

    abstract void runProcesses();

    abstract String getTaskGroupName()

    /**
     * Delay in ms
     */
    public long sleepTime = 600_000;

    void askToRunNewThread() throws Exception {

        Runnable r = {
            try {
                askToRun()
            } catch (Throwable e) {
                onException(e)
            }
        }
        new Thread(r, "${taskGroupName}").start()
    }

    void onException(Throwable e) {
        net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed run ${taskGroupName}", e);
    }

    /**
     * avoid error, if display disconnected
     * @see sun.awt.Win32GraphicsEnvironment#getDefaultScreenDevice
     */
    static boolean isGcOk() {
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        return screenDevices != null && screenDevices.length > 0;
    }

    void askToRun() throws Exception {
        AskUserRunTaskAux askUserRunTaskAux = new AskUserRunTaskAux(this)
        askUserRunTaskAuxLatest = askUserRunTaskAux;
        askUserRunTaskAux.askToRun()
    }

}
