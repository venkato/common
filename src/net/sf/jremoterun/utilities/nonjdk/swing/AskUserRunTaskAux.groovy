package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs

import javax.imageio.ImageIO
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.SwingUtilities
import java.awt.BorderLayout
import java.awt.Image
import java.awt.Window
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class AskUserRunTaskAux {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public AskUserRunTask askUserRunTask;
    public volatile boolean continueFlag = true;
    public volatile boolean suspendFlag = false;
    public Date startDate = new Date()
    public final Object lock = new Object()
    public MyTextArea textArea = new MyTextArea(false);
    public final JDialog dialog = new JDialog((Window) null);
    public volatile boolean suspendPress = false
    public JButton cancelButton = new JButton("Cancel");
    public JButton runNowButton = new JButton("Run now");
    public JButton suspend = new JButton("Suspend");
    public SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    public Date runAtDate = new Date(System.currentTimeMillis() + askUserRunTask.sleepTime);
    public static FileChildLazyRef appRunnerImageRef = GitSomeRefs.commonUtil.childL('resources/icons/app_starter.png')
    public static Image iconImage

    static void setDefaultIconImage(){
        iconImage = ImageIO.read(appRunnerImageRef.resolveToFile());
    }

    AskUserRunTaskAux(AskUserRunTask askUserRunTask) {
        this.askUserRunTask = askUserRunTask
        dialog.setLayout(new BorderLayout());
        dialog.setAlwaysOnTop(true);
        dialog.add(textArea, BorderLayout.CENTER);
        dialog.setSize(200, 30);
        dialog.setLocation(300, 300);
        if(iconImage!=null) {
            dialog.setIconImage(iconImage);
        }
    }

    void askToRun() throws Exception {
        setText()
        prepareDialog()
        showDialog()
        wait1()
        afterWait()
    }

    void setText() {
        textArea.setText("${askUserRunTask.taskGroupName} task, that will be started at " + sdf.format(runAtDate));
    }

    void showDialog() {
        dialog.pack();
        SwingUtilities.invokeLater {
            dialog.setVisible(true);
        };
    }


    void prepareDialog() {
        if (true) {

            cancelButton.addActionListener {
                synchronized (lock) {
                    continueFlag = false;
                    notify1();
                }
                log.info("${askUserRunTask.taskGroupName} ${continueFlag}");
            }
            dialog.add(cancelButton, BorderLayout.NORTH);
        }
        if (true) {

            runNowButton.addActionListener {
                synchronized (lock) {
                    log.info "Run now pressed"
                    suspendFlag = false
                    continueFlag = true;
                    notify1();

                }
                log.info("${askUserRunTask.taskGroupName} ${continueFlag} ${suspendFlag}");
            }
            dialog.add(runNowButton, BorderLayout.EAST);
        }
        if (true) {

            suspend.addActionListener {
                Date d = new Date();
                textArea.setText("${askUserRunTask.taskGroupName} task suspended at ${sdf.format(d)}");
                suspendFlag = true
                synchronized (lock) {
                    suspendFlag = true;
                    notify1();
                }
                log.info("${askUserRunTask.taskGroupName} ${continueFlag} ${suspendFlag}");
            }
            dialog.add(suspend, BorderLayout.SOUTH);
        }
    }

    void wait1() {
        synchronized (lock) {
            lock.wait(askUserRunTask.sleepTime);
            if (suspendFlag) {
                suspendPress = true;
                lock.wait()
            }
        }
    }

    public long diff1 = 60_000

    void afterWait() {
        log.info("${askUserRunTask.taskGroupName} ${continueFlag}");
        SwingUtilities.invokeLater {
            dialog.dispose();
        }
        long diff3 = System.currentTimeMillis() - startDate.getTime()
        log.info "${diff3} ${askUserRunTask.sleepTime}"
        if (!suspendPress && diff3 > askUserRunTask.sleepTime + diff1) {
            log.info "${askUserRunTask.taskGroupName} stange diff ${diff3} ${askUserRunTask.sleepTime} ${new Date()}"
        } else {
            if (continueFlag) {
                askUserRunTask.runProcesses();
            }
        }
    }

    void notify1() {
        lock.notify();
    }

}
