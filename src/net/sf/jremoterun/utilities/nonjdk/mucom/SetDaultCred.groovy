package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JrrUtilities3Swing

import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import java.awt.Window;
import java.util.logging.Logger;

@CompileStatic
class SetDaultCred {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    SetDaultCred() {
    }

    void setVisible(boolean visible1){
        log.info("com.mucommander set visible = ${visible1} ..")
        JrrSwingUtils.setWindowVisibleStartWith('com.mucommander',visible1)
        log.info("com.mucommander set visible = ${visible1} done")
    }


    public String mainFrameStartWith = 'com.mucommander.'
    public ClRef mainFrameClName = new ClRef('com.mucommander.ui.main.MainFrame')
    public ClRef preloadmainFrameClName = new ClRef('com.mucommander.preload.PreloadedJFrame')

    boolean isWindowMatched(Window window1){
        return window1.getClass().getName() == preloadmainFrameClName.className
    }

    JFrame findMainFrame() {
        Collection<Window> windowsAll = JrrUtilities3Swing.findVisibleAwtWindows();
        if(windowsAll.size()==0){
            throw new Exception('No windowsAll found')
        }
        Collection<Window> windowsMatched = windowsAll.findAll { isWindowMatched(it) }
        int size2 = windowsMatched.size()
        if(size2==0){
            List<String> windosAll = windowsAll.collect { it.getClass().getName() }
            throw new Exception("No matched window found : ${windosAll.join(',')}")
        }
        if(size2>1){
            List<String> collectMacthed = windowsMatched.collect { it.getClass().getName() }
            throw new Exception("Many matched window found : ${collectMacthed.join(',')}")
        }
        assert size2 == 1
        JFrame windowMain = windowsMatched[0] as JFrame
        return windowMain;
    }

    JTextField findLeftTextFieldLocation(){
        JFrame windowMain = findMainFrame()
        Object mainFrame1=JrrClassUtils.invokeJavaMethod(windowMain,'getMainFrameObject')
        Object leftPanel1 = JrrClassUtils.invokeJavaMethod(mainFrame1, 'getLeftPanel')
        JTextField locationFiel1 = JrrClassUtils.invokeJavaMethod(leftPanel1, 'getLocationTextField') as JTextField
        return locationFiel1
    }


    void setDir(String newLoc) {
        JTextField locationFiel1 = findLeftTextFieldLocation()
        locationFiel1.setText(newLoc);
        JrrClassUtils.invokeJavaMethod(locationFiel1, 'textFieldValidated');
    }

    ClassLoader findMainClassLoader(){
        JFrame mainFrame = findMainFrame()
        Object obj1=JrrClassUtils.getFieldValue(mainFrame,'mainFrameObj')
        return obj1.getClass().getClassLoader()
    }

    public static ClRef clRefFileUrl = new ClRef('com.mucommander.commons.file.FileURL')
    Object findProtocolHandler(String protocol) {
        Class classFileUtl1 = clRefFileUrl.loadClass(findMainClassLoader());
        Object sftpHandler = JrrClassUtils.invokeJavaMethod(classFileUtl1, 'getRegisteredHandler', protocol)
        if(sftpHandler==null){
            throw new Exception('protocol not found = '+protocol)
        }
        return sftpHandler
    }

    void setCredForProtocol(String user, String password, String protocol) {
        Object sftpHandler = findProtocolHandler(protocol)
        Object cred1 = createCred(user, password)
        assert cred1!=null
        JrrClassUtils.setFieldValue(sftpHandler, 'guestCredentials', cred1);
    }

    public static ClRef clRefCred = new ClRef('com.mucommander.commons.file.Credentials')
    Object createCred(String user, String password) {

        Object cred1 = JrrClassUtils.invokeConstructor(clRefCred.loadClass(findMainClassLoader()), user, password)
        assert cred1!=null
        return cred1
    }


}
