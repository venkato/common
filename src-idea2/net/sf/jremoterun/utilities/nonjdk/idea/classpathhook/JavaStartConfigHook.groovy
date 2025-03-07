package net.sf.jremoterun.utilities.nonjdk.idea.classpathhook


import com.intellij.execution.impl.SingleConfigurationConfigurable
import com.intellij.ide.actions.OpenFileAction
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.Ideasettings.IdeaJavaRunner2Settings
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import javassist.CtClass
import javassist.CtConstructor
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.CodeInjector
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.swing.swingfind.SwingComponentFinder
import org.apache.commons.io.FilenameUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt.*

@CompileStatic
class JavaStartConfigHook extends InjectedCode {
    //private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    Object getImpl(Object key) {
        SwingUtilities.invokeLater { getImpl2(key) }
        return null
    }

    @Override
    protected Object handleException(Object key, Throwable throwable) {
        super.handleException(key, throwable)
        net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Can't create java start hook",throwable)
        return null
    }

    static void installHook() {
        Class clazz = SingleConfigurationConfigurable
        CtClass ctClass = JrrJavassistUtils.getClassFromDefaultPool(clazz)
        CtConstructor method = JrrJavassistUtils.findConstructorByCount(clazz,  ctClass, 2)
        method.insertAfter """
            ${CodeInjector.createSharedObjectsHookVar2(clazz)}
            ${CodeInjector.myHookVar}.get(this);
        """
        CodeInjector.putInector2(clazz, new JavaStartConfigHook());
        JrrJavassistUtils.redefineClass(ctClass, clazz);
    }

    public static boolean doIsokComponentCheck = false;

    void getImpl2(Object o) {
        com.intellij.execution.impl.SingleConfigurationConfigurable c = o as SingleConfigurationConfigurable;
        SettingsEditor settingsEditor = c.getEditor()
        JComponent component = settingsEditor.getComponent()
        log.info "layout 2 ${component.layout} ${c.nameText} ${c.displayName}"
        if(doIsokComponentCheck) {
            Component component1 = SwingComponentFinder.findComponent(component, this.&isOkCOmponent)
            assert component1 != null
        }
        Container parent = component.parent;
        log.info "${parent.layout} "
        GridBagLayout layout2 = parent.layout as GridBagLayout
        //log.info "${layout2.}"
        GridBagConstraints con = new GridBagConstraints();
        con.ipady = 0
        con.anchor =GridBagConstraints.FIRST_LINE_START
        con.fill =GridBagConstraints.HORIZONTAL
        //con.gridx = 3
        con.gridy = 1
        //log.info "${layout2.} "
        JPanel panel = createPanel(c.displayName)
        parent.add(panel,con)
    }

    static String none= 'none'
    
    JPanel createPanel(String runnerName){
        JPanel panel =new JPanel(new BorderLayout())
        //java.util.List<String> list1= new ArrayList(Arrays.asList( IdeaJavaRunnerSettings.libs.list()))
        if(!IdeaJavaRunner2Settings.libs.exists()){
            throw new FileNotFoundException(IdeaJavaRunner2Settings.libs.getAbsolutePath());
        }
        if(!IdeaJavaRunner2Settings.libs.isDirectory()){
            throw new IOException("Not a dir : "+IdeaJavaRunner2Settings.libs.getAbsolutePath());
        }
        java.util.List<String> list1= IdeaJavaRunner2Settings.libs.listFiles().toList().collect {FilenameUtils.getBaseName(it.name)}
        list1.add(none)
        File runnerFile = new File(IdeaJavaRunner2Settings.runners, runnerName)
        JList list = new JList(list1.toArray())
        ListSelectionListener listener = new ListSelectionListener(){
            @Override
            void valueChanged(ListSelectionEvent e) {
                String string = list.selectedValue.toString()

                if (string == none) {
                    runnerFile.delete()
                    assert !runnerFile.exists()
                }else{
                    runnerFile.text = string
                }
            }
        }

        if(runnerFile.exists()){
            boolean hilightError = false
            String selectLib = runnerFile.text
            log.info "selected lib ${selectLib}"
            int i = list1.indexOf(selectLib)
            if(i>=0) {
                list.selectedIndex = i
                File selectedLib2 = new File(IdeaJavaRunner2Settings.libs, selectLib+ ClassNameSuffixes.dotgroovy.customName)
                hilightError = !selectedLib2.exists()
                log.info "selected lib not exists : ${selectedLib2}"
            }else {
                log.info "selected lib not found ${selectLib} in ${list1} , index = ${i}"
                hilightError = true
            }
            if(hilightError){
                list.background = Color.RED
            }
        }
        list.addListSelectionListener(listener)
        list.setVisibleRowCount(1)
        JButton openSelected = new JButton('Open lib config')
        openSelected.addActionListener{
            String selectLib = (String)list.getSelectedValue()
            File selectedLib2 = new File(IdeaJavaRunner2Settings.libs, selectLib+ClassNameSuffixes.dotgroovy.customName)
            openFile(selectedLib2,null);
        }
        panel.add(list,BorderLayout.CENTER)
        panel.add(openSelected,BorderLayout.EAST)
        return panel
    }


    public void openFile(File file, String s) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new FileNotFoundException("Not a file : " + file.getAbsolutePath());
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (virtualFile == null) {
            throw new Exception("virtual file is null");
        }
        if (!virtualFile.isValid()) {
            throw new Exception("virtual file not valid");
        }

        Project project = OSIntegrationIdea.getOpenedProject();
        OpenFileAction.openFile(virtualFile, project);
    }




    boolean isOkCOmponent(Component component){
        if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            return checkBox.text == 'Activate tool window'
        }
        return false;
    }


}
