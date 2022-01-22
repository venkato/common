package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.rstacore.GroovyShellGuiRSyntaxTextArea
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.Message
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException

import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.text.BadLocationException
import java.awt.BorderLayout
import java.awt.Component
import java.awt.FlowLayout
import java.awt.event.KeyEvent
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class RstaRunner extends GroovyShellGuiRSyntaxTextArea{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JPanel panelButtons = new JPanel(new FlowLayout())
    JPanel panel = new JPanel(new BorderLayout()) {

        @Override
        public boolean requestFocusInWindow() {
            return requestFocusInWindow2()
        }

    }

    GroovyShellGuiRSyntaxTextArea textAreaRunner = this;

    JButton runButton = new JButton(RunnerStatus.Run.name())
    JButton saveToFileButton = new JButton("Save to file")
    Thread thread;
    boolean onStopCallInterrupt;
    boolean runInSwingThread = false;
    JTextField progressLable = new JTextField() {

        @Override
        public boolean requestFocusInWindow() {
            return requestFocusInWindow2()
        }

    }
    Component getMainPanel() {
        return panel
    }


    boolean requestFocusInWindow2() {
        return textAreaRunner.getComponent().requestFocusInWindow()
    }

    ClassLoader classLoader2 = JrrClassUtils.getCurrentClassLoader()

    volatile RstaScriptHelper groovyScriptObject;
//    String generatedClassName2 = "RstaRunner"
//    Binding binding = new Binding();
    volatile boolean stopFlag = false;
//    RunnerStatus runnerStatus = RunnerStatus.Stop

    public Runnable codeFinisedFineListener;

    public File fileWithConfig

    RstaRunner(File file) {
        this(checkFileExist2(file).text)
        fileWithConfig = file
        panelButtons.add(saveToFileButton)
        saveToFileButton.addActionListener {
            fileWithConfig.text = textAreaRunner.textArea.getTextNormalized();
        }
    }


    static File checkFileExist2(File file){
        JrrUtilitiesFile.checkFileExist(file)
        if(!file.isFile()){
            throw new IOException("Bad file : ${file}")
        }
        return file
    }

    RstaRunner(String text2) {
        super()
        runButton.setMnemonic(KeyEvent.VK_R);
        panel.add(panelButtons, BorderLayout.NORTH)
        panel.add(textAreaRunner.getComponent(), BorderLayout.CENTER)
        progressLable.setEditable(false)
        progressLable.setColumns(20)
        panelButtons.add(progressLable)
        panelButtons.add(runButton)
        addLangSupport()
        textAreaRunner.textArea.setText(text2)
        runButton.addActionListener {
            try {
                runButtonPressed()
            }catch(Exception e){
                log.log(Level.SEVERE,'failed on manual change status',e)
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException('failed on manual change status',e)
            }
        }
    }


//    @Override
//    public void addLangSupport() throws Exception {
//        textArea.addLangSupport()
//    }

    void prepareAndRun2(Object param) {
        stopFlag = false;
        textAreaRunner.prepareAndRun()
        progressLable.text = ""
        runButton.setEnabled(false)
        Runnable r = {
            runCode(param)
        }
        if (runInSwingThread) {
            r.run()
        } else {
            thread = new Thread(r, "RstaRunner")
            thread.start();
        }

    }

    void enableButtons(){

    }
    void disableButtons(){

    }

    private void runButtonPressed() {
        log.fine("Start time : ${new Date()}");
        requestFocusInWindow2()
        RunnerStatus groovyRunnerState = RunnerStatus.valueOf(runButton.getText());
        switch (groovyRunnerState) {
            case RunnerStatus.Stop:
                runButton.setText(RunnerStatus.Stoping.name());
                runButton.setEnabled(false);
                onStopRequest()
                break;
            case RunnerStatus.Run:
                prepareAndRun2(null);
                break;
            case RunnerStatus.Stoping:
                log.info("still stopping");
                break;
        }
    }

    public volatile Runnable onStopRequestedRunnable
    public volatile List<Closeable> closableRequests = []

    void onStopRequest(){
        stopFlag = true;
        if(onStopRequestedRunnable!=null){
            onStopRequestedRunnable.run()
        }
        closableRequests.each {
            try {
                it.close()
            }catch(Throwable e){
                log.log(Level.WARNING,"failed run close on ${it}",e)
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed run close on ${it}",e)
            }
        }
        if(onStopCallInterrupt) {
            interruptThread();
        }
    }

    void cleanupRefs(){
        onStopRequestedRunnable = null
        closableRequests.clear()
    }

    void interruptThread(){
        Thread thread2 = thread;
        if(thread2!=null && thread2!= Thread.currentThread()) {
            thread2.interrupt();
        }
    }



    void setStatus(String status) {
        SwingUtilities.invokeLater {
            progressLable.setText(status)
        }
    }

    // t:dual
    String getTextToRun(){
        String text2= textAreaRunner.textArea.getTextNormalized()
        if(text2.replace('\r','').replace('\n','').replace('\t','').trim().isEmpty()){
            throw new Exception("no code  : ${text2}")
        }
        return text2
    }

    void runCode(Object param) {
        try {
            StopRequestIndicator.stopRequest.set(this)
            String text2 = getTextToRun();

            Thread.currentThread().setContextClassLoader(classLoader2);
            GroovyClassLoader classLoader = createGroovyClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            String genClassName = textAreaRunner.generatedClassName
            if (genClassName == null) {
                genClassName = 'script' + System.currentTimeMillis() +
                        Math.abs(text2.hashCode()) + ClassNameSuffixes.dotgroovy.customName
            }
            textAreaRunner.generatedClassName = genClassName
            Class parseClass = classLoader.parseClass(text2, genClassName);
//            Thread.currentThread().setContextClassLoader(parseClass.getClassLoader());
            textAreaRunner.generatedClassName = parseClass.getName()
            textAreaRunner.codeStarted()
            runGroovyClass(parseClass,param)
            if (codeFinisedFineListener != null) {
                codeFinisedFineListener.run()
            }
            textAreaRunner.codeStopped()
        } catch (Throwable e) {
            exceptionOccured(e)
            textAreaRunner.codeStopped()
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("", e)
            SwingUtilities.invokeLater { parseException(e) }
        } finally {
            cleanupRefs()
            SwingUtilities.invokeLater {
                runButton.setEnabled( true)
                runButton.setText(RunnerStatus.Run.name())
                enableButtons()
            }
        }
    }

    void exceptionOccured(Throwable e){

    }

    GroovyClassLoader createGroovyClassLoader() {
        ClassLoader currentClassLoader = classLoader2
        if (classLoader2 instanceof GroovyClassLoader) {
            return (GroovyClassLoader) currentClassLoader;

        }
        return new GroovyClassLoader(classLoader2)
    }

    protected void parseException(Throwable e2) throws BadLocationException {
        if (e2 instanceof MultipleCompilationErrorsException) {
            MultipleCompilationErrorsException f1 = (MultipleCompilationErrorsException) e2;
            ErrorCollector errorCollector = f1.getErrorCollector();
            java.util.List<Message> errors = (List)errorCollector.getErrors();
            if (errors.size() > 0) {
                Message message = errors.get(0);

                if (message instanceof SyntaxErrorMessage) {
                    SyntaxErrorMessage syntaxErrorMessage = (SyntaxErrorMessage) message;
                    SyntaxException cause = syntaxErrorMessage.getCause();
                    log.info "${cause.getSourceLocator()} ${textAreaRunner.generatedClassName}"
                    if (textAreaRunner.generatedClassName == cause.getSourceLocator()) {
                        int line = cause.getStartLine();
                        log.info "${line}"
                        line += -1;
                        textAreaRunner.highLightLineAsError(line);
                        return
                    } else {
                        log.info("ignore SyntaxException as another groovy script : " + cause.getSourceLocator());
                    }
                }
            }
        }
        Throwable rootException = JrrUtils.getRootException(e2);
        StackTraceElement[] stackTraces = rootException.getStackTrace();
        final int lineNumber2 = getLineNumberFromStackTrace4(stackTraces);
        if (lineNumber2 != -1) {
            textAreaRunner.highLightLineAsError(lineNumber2);
        }
    }

    int getLineNumberFromStackTrace4(StackTraceElement[] stackTraces) {
        if(textAreaRunner.generatedClassName==null ||textAreaRunner.generatedClassName.length()==0){
            return -1;
        }
        StackTraceElement stackTraceElementF = null;
        for (StackTraceElement stackTraceElement : stackTraces) {
            if (stackTraceElement.getClassName().startsWith(textAreaRunner.generatedClassName)) {
                stackTraceElementF = stackTraceElement;
                break;
            }
        }
        if (stackTraceElementF != null) {
            int lineNumber = stackTraceElementF.getLineNumber();
            final int lineNumber2 = lineNumber - 1;
            // log.info(lineNumber2);
            return lineNumber2;
        }
        return -1;
    }

    Object runGroovyClass(Class scriptClass,Object param) throws Exception {
        groovyScriptObject = scriptClass.newInstance() as RstaScriptHelper;
        groovyScriptObject.runner = this;
        assert groovyScriptObject.runner != null
//        groovyScriptObject.setBinding(binding)
        assert groovyScriptObject.runner != null
        SwingUtilities.invokeLater {
            if (runButton.isEnabled()) {
                log.info("not enabled ${runButton.getText()}")
            } else {
                disableButtons()
                runButton.setText(RunnerStatus.Stop.name());
                runButton.setEnabled(true)
            }
        }
        preRunAfterScriptLoaded()
        groovyScriptObject.run()
        return null;
    }

    void preRunAfterScriptLoaded(){

    }


}