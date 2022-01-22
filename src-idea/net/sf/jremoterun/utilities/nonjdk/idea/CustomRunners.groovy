package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderGeneric
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.idea.jrr.JrrIdeaBeanCommon
import net.sf.jremoterun.utilities.nonjdk.iderunners.IdeScriptRunner
import net.sf.jremoterun.utilities.nonjdk.iderunners.ScriptLogInterface
import net.sf.jremoterun.utilities.nonjdk.lang.AfterResultReadyDummy

import javax.swing.*
import java.awt.*
import java.util.List
import java.util.logging.Logger

@CompileStatic
class CustomRunners {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Icon icon = IconLoader.getIcon('/icon/idea/custom_runners.png', CustomRunners);

    public static OSIntegrationIdea osInegrationClient;


    public static File runnersDir

    public static GroovyConfigLoaderGeneric jrrGroovyScriptRunner = new GroovyConfigLoaderGeneric();
    public static JTextField logTextField = new JTextField('', 30)

    public static ScriptLogInterface si = new ScriptLogInterface() {
        @Override
        void info(Object obj) {
            String msg1 = "${logTextField.getText()} ${obj}"
            SwingUtilities.invokeLater {
                logTextField.setText(msg1)
            }
        }
    }


    static void createCustomRunners3(File runnersDir2) {
        createCustomRunners2(OSIntegrationIdea.osIntegrationIdea, runnersDir2)
    }

    static void createCustomRunners2(OSIntegrationIdea osInegrationClient2, File runnersDir2) {
        logTextField.setEditable(false)
        assert runnersDir2.exists()
        runnersDir = runnersDir2
        assert osInegrationClient2 != null
        osInegrationClient = osInegrationClient2
//        jrrGroovyScriptRunner.initDigest()
        JPanel panel2 = new JPanel(new FlowLayout())
        JButton refreshButton = new JButton("Refresh")
        refreshButton.addActionListener {
            refreshButton.setEnabled(false)
            try {
                refresh(panel2)
            } finally {
                refreshButton.setEnabled(true)
            }
        }
        refresh(panel2)
        JPanel panel = createCustomRunners();
        panel.add(refreshButton, BorderLayout.NORTH)
        panel.add(panel2, BorderLayout.CENTER)

    }

    static void refresh(JPanel panel2) {
        panel2.removeAll()
        panel2.add(logTextField)
        List<File> all = runnersDir.listFiles().toList().findAll { it.isFile() && it.name.endsWith(ClassNameSuffixes.dotgroovy.customName) }
        all.each {
            File f = it
            panel2.add(createActionButton(f));
            JButton openFileButton = new JButton("O")
            panel2.add(openFileButton)
            openFileButton.addActionListener {
                osInegrationClient.openFile(f, null);
            }
        }


    }

    static JButton createActionButton(File f) {
        JButton button = new JButton(f.name.replace(ClassNameSuffixes.dotgroovy.customName, ''))
        button.addActionListener {
            AfterResultReadyDummy r22 = new AfterResultReadyDummy()
            osInegrationClient.saveAllAsync(r22)
            button.setEnabled(false)
            logTextField.setText('')
            Runnable r = {
                try {
                    log.info "file ${f} calling .."
                    ContextClassLoaderWrapper.wrap2(jrrGroovyScriptRunner.groovyClassLoader) {
                        Object instance = jrrGroovyScriptRunner.parseConfig(f)
//                        Thread.currentThread().setContextClassLoader(clazz.getClassLoader())
//                        Object instance = clazz.newInstance()
                        if (instance instanceof IdeScriptRunner) {
                            instance.run2(si)
                        } else if (instance instanceof Runnable) {
                            instance.run()
                        } else {
                            JrrClassUtils.invokeJavaMethodR(new ClRef(Runnable), instance, "run")
                        }
                        log.info "file ${f} called"
                    }
                } catch (Throwable e) {
                    log.info "${f} ${e}"
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(f.name, e)
                } finally {
                    SwingUtilities.invokeLater {
                        button.setEnabled(true)
                    }
                }
            }
            Thread thread = new Thread(r, "${f.name} custom runner")
            thread.start()
        }
        return button;
    }


    private static JPanel createCustomRunners() {
        JPanel panel = JrrIdeaBeanCommon.bean.customRunners
        if (panel != null) {
            return panel
        }
        Project project = getOpenedProject()
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project)
        panel = new JPanel(new BorderLayout())
        JrrIdeaBeanCommon.bean.customRunnersToolWindow = toolWindowManager.registerToolWindow('Custom runners', panel, ToolWindowAnchor.RIGHT)
        JrrIdeaBeanCommon.bean.customRunners = panel
        JrrIdeaBeanCommon.bean.customRunnersToolWindow.icon = icon
        return panel
    }


    static Project getOpenedProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects == null || openProjects.length == 0) {
            throw new IllegalStateException("Can't find open project");
        }
        return openProjects[0];

    }


}
