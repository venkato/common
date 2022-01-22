package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.QrCodeCreator
import org.apache.commons.codec.binary.Base64

import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import java.awt.BorderLayout
import java.nio.charset.Charset
import java.util.logging.Logger

@CompileStatic
class Base64PaneCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public TextAreaAndView areaAndView = new TextAreaAndView('Text')
    public JCheckBox encodeCheckBox = new JCheckBox('Encode', true)
    public TextAreaAndView qrCodeCreator = new TextAreaAndView('Result');

    public SplitWindow splitWindow = new SplitWindow(true, areaAndView.view, qrCodeCreator.view)
    public static String titleDefault =  'Base64Decoder'

    Base64PaneCreator() {
        addListener1()
        areaAndView.panel.add(encodeCheckBox, BorderLayout.NORTH)
        IdwUtils.setTitle(splitWindow, titleDefault)
    }

    void addListener1() {
        areaAndView.textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            void insertUpdate(DocumentEvent e) {
                updateText()
            }

            @Override
            void removeUpdate(DocumentEvent e) {
                updateText()
            }

            @Override
            void changedUpdate(DocumentEvent e) {
                updateText()
            }

        })
    }

    String convert(String text1, boolean selectedEncode) {
        return convertJavaJre(text1,selectedEncode)
    }

    String convertJavaJre(String text1, boolean selectedEncode) {
        if (selectedEncode) {
            return  java.util.Base64.getEncoder().encodeToString(text1.getBytes(defaultCharset))
        } else {
            byte[] base641 = java.util.Base64.getDecoder().decode(text1)
            return new String(base641,defaultCharset)
        }
    }

    String convertCommons(String text1, boolean selectedEncode) {
        if (selectedEncode) {
            return  Base64.encodeBase64String(text1.getBytes(defaultCharset))
        } else {
            byte[] base641 = Base64.decodeBase64(text1)
            return new String(base641,defaultCharset)
        }
    }


    public Charset defaultCharset = Charset.defaultCharset()


    void updateText() {

        SwingUtilities.invokeLater {

            String text1 = areaAndView.textArea.getText()
            String result1
            boolean selected1 = encodeCheckBox.isSelected()
            try {
                result1 = convert(text1, selected1)
            } catch (Exception e) {
                log.info(text1, e)
                result1 = net.sf.jremoterun.JrrUtils.exceptionRootToString(e)
//                result1 = e.toString()
            }
            qrCodeCreator.textArea.setText(result1)
        }
    }

}
