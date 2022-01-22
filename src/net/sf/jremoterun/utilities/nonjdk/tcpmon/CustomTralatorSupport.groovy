package net.sf.jremoterun.utilities.nonjdk.tcpmon

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyClassLoaderDefault
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.rstacore.RSyntaxTextAreaCodeAssistUndoFix
import net.sf.jremoterun.utilities.nonjdk.swing.JPanel4FlowLayout
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPopupMenu
import javax.swing.ScrollPaneConstants;
import java.util.logging.Logger;

@CompileStatic
class CustomTralatorSupport {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JButton applyConfigButton = new JButton('Apply config')
    JButton loadConfigFromFileButton = new JButton('Load config')
    File configConnectionDir;
    JComboBox<String> otherConfigConnectInDir
    public static String groovySuffix = ClassNameSuffixes.dotgroovy.customName
    public JPanel4FlowLayout panel4FlowLayout = new JPanel4FlowLayout();
//    public File fileWithConnectionConfig;
    public GroovyClassLoader groovyClassLoader

    Listener listener;


    public RSyntaxTextAreaCodeAssistUndoFix connectRunnerTextArea = new RSyntaxTextAreaCodeAssistUndoFix() {
        @Override
        void appendFoldingMenu2(JPopupMenu popupMenu) {
        }
    };


    CustomTralatorSupport(Listener listener) {
        this.listener = listener
        connectRunnerTextArea.scrollPane = new RTextScrollPane(connectRunnerTextArea, true);
    }


    void f1(File connectFile) {
        assert connectFile.exists()
//        fileWithConnectionConfig = connectFile;

        connectRunnerTextArea.setText(connectFile.text)

        connectRunnerTextArea.setTabSize(2);
        connectRunnerTextArea.setCodeFoldingEnabled(true)
        connectRunnerTextArea.addLangSupport()
        connectRunnerTextArea.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        connectRunnerTextArea.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        connectRunnerTextArea.scrollPane.setIconRowHeaderEnabled(true);
        configConnectionDir = connectFile.getParentFile();
        assert configConnectionDir.exists();

        List<File> files = configConnectionDir.listFiles().toList().findAll { it.getName().endsWith(groovySuffix) }
        List<String> names = files.collect { it.getName().replace(groovySuffix, '') }
        String[] namesArray = names.toArray(new String[0])
        otherConfigConnectInDir = new JComboBox<String>(namesArray);
        otherConfigConnectInDir.setSelectedItem(connectFile.getName().replace(groovySuffix, ''))
        loadConfigFromFileButton.addActionListener { loadConfigFromFile() }
        applyConfigButton.addActionListener { applyConfig(); }
        panel4FlowLayout.add(otherConfigConnectInDir)
        panel4FlowLayout.add(applyConfigButton)
        panel4FlowLayout.add(loadConfigFromFileButton)
    }

//    void saveConfigM() {
//        try {
//            fileWithConnectionConfig.text = connectRunnerTextArea.getTextNormalized();
//        } catch (Throwable e) {
//            log.error3('failed load', e)
//            JrrUtilitiesShowE.showException('failed load', e)
//        }
//    }

    void applyConfig() {
        try {
            listener.tcpMonSettings.customDataTralator = loadConfigImpl();
        } catch (Throwable e) {
            log.error3('failed apply', e)
            JrrUtilitiesShowE.showException('failed apply', e)
        }
    }

    void loadConfigFromFile() {
        try {
            String aa = otherConfigConnectInDir.getSelectedItem();
            File f = new File(configConnectionDir,aa+groovySuffix)
            assert f.exists()
            connectRunnerTextArea.setText(f.text)
        } catch (Throwable e) {
            log.error3('failed load', e)
            JrrUtilitiesShowE.showException('failed load', e)
        }
    }

    CustomDataTralator loadConfigImpl() {
        if (groovyClassLoader == null) {
            groovyClassLoader = GroovyClassLoaderDefault.receiveGroovyClassLoader2()
        }
        String normalized = connectRunnerTextArea.getTextNormalized()
        Class clazz = groovyClassLoader.parseClass(normalized)
        CustomDataTralator cdt = clazz.newInstance() as CustomDataTralator
        return cdt
    }


}
