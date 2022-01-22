package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.swing.JrrSwingUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JrrUtilities3Swing

import javax.swing.JFrame
import javax.swing.JTextField
import java.awt.Window;
import java.util.logging.Logger;

@CompileStatic
class SetDaultCred {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static   ClRef mainClass = new ClRef('com.mucommander.muCommander')
    public static   ClRef main2Class = new ClRef('com.mucommander.main.muCommander')

    public static ClRef clRefFileUrl = new ClRef('com.mucommander.commons.file.FileURL')

    public static String mainFrameStartWith = 'com.mucommander.'

    public static ClRef mainFrameClName = new ClRef('com.mucommander.ui.main.MainFrame')

    public static ClRef preloadmainFrameClName = new ClRef('com.mucommander.preload.PreloadedJFrame')

    public static ClRef clRefCred = new ClRef('com.mucommander.commons.file.Credentials')

    SetDaultCred() {
    }

    void setVisible(boolean visible1){
        log.info("com.mucommander set visible = ${visible1} ..")
        JrrSwingUtils.setWindowVisibleStartWith('com.mucommander',visible1)
        log.info("com.mucommander set visible = ${visible1} done")
    }

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
        Object mainFrame1=findMainFrameObject()
        Object leftPanel1 = JrrClassUtils.invokeJavaMethodR(mainFrameClName,mainFrame1, 'getLeftPanel')
        JTextField locationFiel1 = JrrClassUtils.invokeJavaMethodR(new ClRef('com.mucommander.ui.main.FolderPanel'),leftPanel1, 'getLocationTextField') as JTextField
        return locationFiel1
    }

    Object findMainFrameObject(){
        JFrame windowMain = findMainFrame()
        Object mainFrame1=JrrClassUtils.invokeJavaMethodR(preloadmainFrameClName,windowMain,'getMainFrameObject')
        assert mainFrame1!=null
        return mainFrame1
    }


    void setDir(String newLoc) {
        JTextField locationFiel1 = findLeftTextFieldLocation()
        locationFiel1.setText(newLoc);
        JrrClassUtils.invokeJavaMethodR(new ClRef('com.mucommander.ui.main.LocationTextField'),locationFiel1, 'textFieldValidated');
    }

    ClassLoader findMainClassLoader(){
        Object obj1=findMainFrameObject()
        return obj1.getClass().getClassLoader()
    }

    Object findProtocolHandler(String protocol) {
        Class classFileUtl1 = clRefFileUrl.loadClass(findMainClassLoader());
        Object sftpHandler = JrrClassUtils.invokeJavaMethodR(clRefFileUrl, classFileUtl1, 'getRegisteredHandler', protocol)
        if(sftpHandler==null){
            throw new Exception('protocol not found = '+protocol)
        }
        return sftpHandler
    }

    void setCredForProtocol(String user, String password, String protocol) {
        Object sftpHandler = findProtocolHandler(protocol)
        Object cred1 = createCred(user, password)
        assert cred1!=null
        JrrClassUtils.setFieldValue(sftpHandler, 'guestCredentials', cred1); //NOFIELDCHECK
    }

    Object createCred(String user, String password) {

        Object cred1 = JrrClassUtils.invokeConstructorR(clRefCred, clRefCred.loadClass(findMainClassLoader()), user, password)
        assert cred1!=null
        return cred1
    }


}
