package net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.EnvOpenSettings

import javax.swing.DropMode
import javax.swing.JMenuItem
import javax.swing.JTextField
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

@CompileStatic
class BackupLocationJPanel implements DropFileAware {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JTextField fileTextField = new JTextField('')
    public TextAreaAndView textAreaAndView = new TextAreaAndView('Backup location')
    public BackupDirFactory backupDirFactory = BackupDirFactory.backupDirFactory

    public String analizedFileBefore

    BackupLocationJPanel() {
        init()
    }

    void init() {
        fileTextField.addKeyListener(new KeyAdapter() {
            @Override
            void keyTyped(KeyEvent e) {
                update1()
            }
        })
        textAreaAndView.panel.add(fileTextField, BorderLayout.NORTH)
        JMenuItem menuItem = new JMenuItem('Open location')
        menuItem.addActionListener {
            openLocationAction()
        }
        textAreaAndView.textArea.predefinedMenus.add(menuItem)
        textAreaAndView.textArea.setEditable(false)
        fileTextField.setDropMode(DropMode.INSERT)
        fileTextField.setTransferHandler(new BackupTransferHandler(this));
        fileTextField.addActionListener { openLocationAction() }
    }

    @Override
    void onFileDropped(String aa) {
        fileTextField.setText(aa);
        update1()
    }

    void openLocationAction() {
        int start1 = textAreaAndView.textArea.getSelectionStart()
        String text1 = textAreaAndView.textArea.getText()
        try {
            log.info "${start1}"
            int fwd = text1.indexOf('\n', start1)
            String s4 = text1.substring(0, start1)
            int before = s4.lastIndexOf('\n')
            if (fwd == -1) {
                fwd = text1.length()
            }
            log.info2(before)
            log.info2(fwd)
            String aaa = text1.substring(before + 1, fwd)
            log.info "found text ${aaa}"
            File f = backupDirFactory.baseBackupDir.child(aaa)
            EnvOpenSettings.defaultOpenFileHandler.openFile(f)
        } catch (Exception e) {
            log.info("failed on ${start1} ${text1}", e)
            JrrUtilitiesShowE.showException("failed on ${start1}", e);
        }

    }

    public int textMinLength = 2

    void update1() {
        String s = fileTextField.getText()
        s = s.trim()
        if (s.length() > textMinLength) {
            try {
                if (analizedFileBefore == null || analizedFileBefore != s) {
                    update2(s)
                } else {
                    log.info "same text as before ${s}"
                }
            } catch (Exception e) {
                log.info("on ${s}", e)
                String string1 = JrrUtils.exceptionToString(e)
                textAreaAndView.textArea.setText(string1)
            }
        }
    }

    void update2(String s) {
        File f = new File(s)
        List<File> locations = backupDirFactory.printAllLocations(f)
        String result
        if (locations.size() == 0) {
            result = "not found : ${f.getAbsolutePath()} ."
        } else {
            result = "Found ${locations.size()}\n" + locations.collect { backupDirFactory.baseBackupDir.getPathToParent(it) }.join('\n')
        }
        analizedFileBefore = s
        textAreaAndView.textArea.setText(result)
    }

}
