package net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.TransferHandler
import javax.swing.text.BadLocationException
import javax.swing.text.Document
import javax.swing.text.JTextComponent
import java.awt.Component
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection;
import java.util.logging.Logger;

@CompileStatic
class BackupTransferHandler extends TransferHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DropFileAware backupLocationJPanel;


    BackupTransferHandler(DropFileAware backupLocationJPanel) {
        this.backupLocationJPanel = backupLocationJPanel;
    }

    public boolean canImport(TransferSupport support) {

//        log.info "canImport"
//        if (!support.isDrop()) {
//            return false;
//        }

        DataFlavor[] flavors = support.getDataFlavors()
//        log.info "flavors = ${flavors}"
        if (flavors != null && flavors.length > 0) {
            DataFlavor flavor1 = flavors[0]
            if (flavor1 == java.awt.datatransfer.DataFlavor.javaFileListFlavor) {
                return true;
            }
        }
        return super.canImport(support)
    }


    public boolean importData(TransferSupport support) {
//        log.info "import data1"
        DataFlavor[] flavors = support.getDataFlavors()
//        log.info "flavors = ${flavors}"
        if (flavors != null && flavors.length > 0) {
            DataFlavor flavor1 = flavors[0]
            if (flavor1 == java.awt.datatransfer.DataFlavor.stringFlavor) {
                Object data1 = support.getTransferable().getTransferData(flavor1)
                if(data1 instanceof String){
                    String aa = data1
                    backupLocationJPanel.onFileDropped(aa)
                    return true
                }else{
                    log.info "not a string"
                }
            }else  if (flavor1 == java.awt.datatransfer.DataFlavor.javaFileListFlavor) {
                Object data1 = support.getTransferable().getTransferData(flavor1)
//                log.info "data = ${data1}"
//                log.info "data cl = ${data1.getClass()}"
                if (data1 instanceof List) {
                    List ll = (List) data1;
                    Object el1 = ll[0]
//                    log.info "el1 = ${el1}"
//                    log.info "el1 cl = ${el1.getClass()}"
                    if (el1 instanceof File) {
                        File f4 = (File) el1;
                        log.info "file = ${f4}"
//                        Component component1 = support.getComponent()
//                        DropLocation location = support.getDropLocation()
//                        log.info "drop Loc = ${location}"
//                        log.info "component = ${component1}"
//                        if (component1 instanceof JTextField) {
//                            JTextField text11 = (JTextField) component1;
                        backupLocationJPanel.onFileDropped(f4.getAbsolutePathUnix())
//                        backupLocationJPanel.file1.setText(f4.getAbsolutePathUnix());
//                        backupLocationJPanel.update1()

//                        }
                        return true;
                    }else{
                        log.info "not a file ${el1}"
                    }

                } else {
                    log.info("not a list ${data1}")
                }
            }else{
                log.info "not file flavour ${flavor1}"
            }
        }else{
            log.info "flavour is empty"
        }
        return super.importData(support)

    }


    public void exportToClipboard(JComponent comp, Clipboard clipboard,
                                  int action) throws IllegalStateException {
        if (comp instanceof JTextComponent) {
            JTextComponent text = (JTextComponent)comp;
            int p0 = text.getSelectionStart();
            int p1 = text.getSelectionEnd();
            if (p0 != p1) {
                try {
                    Document doc = text.getDocument();
                    String srcData = doc.getText(p0, p1 - p0);
                    StringSelection contents =new StringSelection(srcData);

                    // this may throw an IllegalStateException,
                    // but it will be caught and handled in the
                    // action that invoked this method
                    clipboard.setContents(contents, null);

                    if (action == TransferHandler.MOVE) {
                        doc.remove(p0, p1 - p0);
                    }
                } catch (BadLocationException ble) {
                    log.info(text,ble)
                }
            }
        }
    }


    public int getSourceActions(JComponent c) {
        return super.getSourceActions(c);
//        return NONE;
    }

}
