package net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.infonode.docking.View
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.DefaultObjectName
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace

import javax.management.ObjectName
import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class AlertTable implements ShowAlertJmx, DefaultObjectName{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public TextAreaAndView detailsView = new TextAreaAndView('Details')
    public DefaultTableModel defaultTableModel = new DefaultTableModel()
    public JTable tableSummary = new JTable(defaultTableModel)
    ObjectName defaultObjectName = ShowAlertJmx.objectName1

    public Vector columnNames = new Vector(AlertTableColumns.values().toList().collect { it.name() })
    JScrollPane tableMsgsScrollPane = new JScrollPane(tableSummary)
    View tableMsgsView = new View('Messages', null, tableMsgsScrollPane);

    public SplitWindow splitWindow = new SplitWindow(true, tableMsgsView, detailsView.view);

    public SimpleDateFormat sdf = new SimpleDateFormat('dd HH:mm')
    public List<String> exceptionList = []
    public int currentRowShowing = -1;
    public JButton showExceptionButton = new JButton('Show exception in IDE')
    public OsInegrationClientI inegrationClient;
    public File saveAlertsToFolder
    public int keepLastNAlertsInDir = 5

    public static AlertTable defaultAlertTable = new AlertTable();
    public static Runnable afterAlertAdded;


    AlertTable() {
        defaultTableModel.setColumnIdentifiers(columnNames);
        tableSummary.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent e) {
                try {
                    showMsg(tableSummary.getSelectedRow())
                } catch (Throwable e2) {
                    log.info("failed show selected msg", e2)
                    onException("failed show selected msg", e2)
                }
            }
        });
        showExceptionButton.addActionListener {
            try {
                String ee = exceptionList.get(currentRowShowing)
                if (inegrationClient == null) {
                    throw new NullPointerException('inegrationClient not set')
                }
                inegrationClient.showStackTrace(ee)
            } catch (Throwable e2) {
                log.info("failed show exception", e2)
                onException("failed show exception", e2)
            }
        }
        detailsView.panel.add(showExceptionButton, BorderLayout.NORTH)
        IdwUtils.setTitle(splitWindow, 'Alert table')
        detailsView.textArea.setLineWrap(true);
        detailsView.textArea.setWrapStyleWord(true);
        detailsView.textArea.setEditable(false);
        MBeanFromJavaBean mBeanFromJavaBean = new MBeanFromJavaBean(this, ShowAlertJmx)
        JrrUtils.findLocalMBeanServer().registerMBean(                mBeanFromJavaBean, defaultObjectName);
    }


    protected void showMsg(int activeColumn) {
        assert activeColumn != -1
        currentRowShowing = activeColumn;
        Vector row = defaultTableModel.getDataVector().get(activeColumn) as Vector
        int i = columnNames.indexOf(AlertTableColumns.message.name())
        String msg = row.get(i);
        detailsView.textArea.setText(msg)
    }

    static void addAlertS(String msg) {
        addAlertS(msg, null)
    }

    static void addAlertS(String msg, Throwable e) {
        defaultAlertTable.addAlert6(msg,e)
    }


    void addAlertInSwingThread(String msg, Throwable e){
        addAlert(msg, e);
        makeWindowVisible()
    }

    void makeWindowVisible(){
        SwingUtilities.invokeLater {
            try {
                splitWindow.makeVisible()
                IdwUtils.setVisible(splitWindow);
                if (afterAlertAdded != null) {
                    afterAlertAdded.run()
                }
            } catch (Throwable e2) {
                log.info("failed make visible", e2)
                onException("failed make visible", e2)
            }
        }

    }

    void addAlert6(String msg, Throwable e){
        if (e == null) {
            e = new JustStackTrace()
        }
//        log.info(msg, e)
        SwingUtilities.invokeLater {
            try {
                addAlertInSwingThread(msg,e)
            } catch (Throwable e2) {
                log.info("failed show selected msg", e2)
                onException("failed show selected msg", e2)
            }
        }
    }

    void onException(String msg, Throwable e2){
        JrrUtilitiesShowE.showException(msg, e2)
    }

    void addAlert(String msg, Throwable e) {
        String excS = JrrUtils.exceptionToString(e)
//        Vector row = new Vector()
        String dateeeAsStr = sdf.format(new Date())
//        row.add(dateeeAsStr);
        msg += " \n ${excS}"
//        row.add(msg);
//        defaultTableModel.addRow(row)
//        exceptionList.add(excS)
//        detailsView.textArea.setText(msg)
//        currentRowShowing = exceptionList.size() - 1
        addAlert2(dateeeAsStr, msg)

        if (saveAlertsToFolder != null) {
            File alertFile = saveAlertsToFolder.child(alertFileName)
            if (alertFile.exists()) {
                FileRotate.rotateFile(alertFile, keepLastNAlertsInDir)
            }
            alertFile.text = dateeeAsStr +' '+ msg
        }
    }

    void addAlert2(String rowMsg, String detailMsg) {
        Vector row = new Vector()
        row.add(rowMsg);
        row.add(detailMsg);
        defaultTableModel.addRow(row)
        exceptionList.add(detailMsg)
        detailsView.textArea.setText(detailMsg)
        currentRowShowing = exceptionList.size() - 1

    }

    String alertFileName = 'latestAlert.txt'

    void loadLastAlerts() {
        int counttt =0
        for (int i in keepLastNAlertsInDir..0) {
            File fileBefore2 = FileRotate.buildRotateFile(saveAlertsToFolder.child(alertFileName), saveAlertsToFolder, i, false)
            log.info "checking file ${fileBefore2}"
            if (fileBefore2.exists()) {
                String text = fileBefore2.text
                String first = text.readLines().first()
                addAlert2('before : ' + first, text)
                counttt++
            }

        }
        log.info "loaded ${counttt}"
    }

    void saveAndLoadLastAlerts(File folder){
        folder.mkdir()
        assert folder.exists()
        saveAlertsToFolder = folder
        loadLastAlerts()
    }


}
