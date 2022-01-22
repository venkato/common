package idea.plugins.thirdparty.filecompletion.share

import com.intellij.execution.Executor
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.RunManagerEx
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.ide.SaveAndSyncHandler
import com.intellij.ide.actions.OpenFileAction
import com.intellij.ide.caches.CachesInvalidator
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.persistent.FSRecords
import com.intellij.psi.*
import com.intellij.psi.search.ProjectAndLibrariesScope
import com.intellij.unscramble.AnalyzeStacktraceUtil
import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.DefaultObjectName
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.ObjectWrapper
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.nonjdk.lang.AfterResultReady
import net.sf.jremoterun.utilities.nonjdk.lang.AfterResultReadyDummy

import javax.management.MalformedObjectNameException
import javax.management.ObjectName
import javax.swing.*
import java.util.concurrent.Callable

/**
 * Tools->InternalAction->UIdebugger
 * @see com.intellij.ide.actions.GotoActionAction
 */
@CompileStatic
public class OSIntegrationIdea implements DefaultObjectName, OsInegrationClientI {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static OSIntegrationIdea osIntegrationIdea

    OSIntegrationIdea() {
        if (osIntegrationIdea == null) {
            osIntegrationIdea = this
        } else {

        }
    }

    @Override
    public ObjectName getDefaultObjectName() throws MalformedObjectNameException {
        return OsInegrationClientI.objectName;
    }

    public static boolean runInNewThread = true;

    @Override
    public void buildAllProjects() throws Exception {
        log.info "buildAllProjects .."
        IdeaReBuilder reBuilder = new IdeaReBuilder();
        AfterResultReady afterResultReady=new AfterResultReadyDummy()
        reBuilder.rebuild(afterResultReady)
        log.info "buildAllProjects method finished"
    }


    DumbService getDumbService() {
        DumbService.getInstance(getOpenedProject())
    }


    void refreshAll() {
        invokeAndWaitInSwingThread {
            refreshImpl()
        }
    }

    void refreshAllAync(){
        JrrIdeaUtils.submitTr{
           refreshImpl()
        }
    }

    static void refreshImpl(){
        SaveAndSyncHandler.getInstance().refreshOpenFiles();
        VirtualFileManager.getInstance().syncRefresh()
    }


    public static long swingInvokeLaterMaxDelay = 600_000

    static void invokeAndWaitInSwingThread(Callable callable) {
        if (SwingUtilities.isEventDispatchThread()) {
            callable.call()
        } else {
            long startTime = System.currentTimeMillis()
            ObjectWrapper<Throwable> exc = new ObjectWrapper<>(null);
            SwingUtilities.invokeAndWait {
                try {
                    long delay = System.currentTimeMillis() - startTime
                    if(delay> swingInvokeLaterMaxDelay){
                        exc.object = new Exception("Delay too big : ${delay} , now : ${new Date()}, start time : ${new Date(startTime)}");
                    }else {
                        callable.call()
                    }
                } catch (Throwable e) {
                    exc.object = e;
                }
            };
            if (exc.object != null) {
                Throwable e = exc.object;
                JrrUtils.throwThrowable(e);
            }
        }
    }

    @Override
    public void clearConsole(String s) throws Exception {

    }

    public void invalidateCacheAndRestart() {
        saveAllEditors()
        FSRecords.invalidateCaches();

        for (CachesInvalidator invalidater : CachesInvalidator.EP_NAME.getExtensions()) {
            invalidater.invalidateCaches();
        }
        final ApplicationEx app = (ApplicationEx) ApplicationManager.getApplication();
        app.restart(false)
    }


    @Override
    public void closeAllEditorsWithoutSave() {
        SwingUtilities.invokeLater {
            try {
                closeAllEditorsWithoutSaveImpl()
            }catch(Exception e){
                log.error("failed close all editors",e);
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed close all editors",e);
            }
        }

    }

    /**
     * @see com.intellij.ide.actions.CloseAllEditorsAction
     */
    public void closeAllEditorsWithoutSaveImpl() {
        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(openedProject);
        VirtualFile[] files4 = fileEditorManager.getSelectedFiles();
        if(files4.length>0) {
            VirtualFile selectedFile = files4[0];
            //Object openFiles2 = fileEditorManager.getSiblings(selectedFile);
            List<VirtualFile> openFiles = convert1(fileEditorManager.getSiblings(selectedFile));
            for (final VirtualFile openFile : openFiles) {
                fileEditorManager.closeFile(openFile);
            }
        }else{
            log.info "no selected files"
        }
    }

    private List<VirtualFile> convert1(Object ooo){
        if (ooo instanceof List) {
            return  (List) ooo;
        }
        VirtualFile[] openFiles = ooo as VirtualFile[]
        return openFiles.toList()
    }

    @Override
    public void saveAllEditors() throws Exception {
//        invokeAndWaitInSwingThread {
//            saveAllImpl()
//        }

        AfterResultReadyDummy r=new AfterResultReadyDummy()
        saveAllAsync(r)
    }


    @Deprecated
    void saveAllImpl(){
        AfterResultReadyDummy afterResultReady =new AfterResultReadyDummy()
        saveAllImpl2(afterResultReady)
    }

    static void saveAllImpl2(AfterResultReady afterResultReady){
        FileDocumentManager.getInstance().saveAllDocuments();
        PsiDocumentManager.getInstance(openedProject).commitAllDocuments();
        boolean allowed = ApplicationManager.getApplication().isWriteAccessAllowed()
        log.info("write access allowed = ${allowed}")
        SwingUtilities.invokeLater {
            try {
                openedProject.save()
                afterResultReady.ready()
            }catch(Throwable e){
                log.warn("failed save project",e)
                JrrUtilitiesShowE.showException("failed save project",e)
                afterResultReady.onException(e)
            }
        }
    }

    void saveAllAsync(AfterResultReady afterResultReady){
        Runnable r = {
            ApplicationManager.getApplication().runWriteAction {

                boolean allowed = ApplicationManager.getApplication().isWriteAccessAllowed()
                log.info("write access allowed = ${allowed}")
                saveAllImpl2(afterResultReady)
            }
        }
        JrrIdeaUtils.submitTr(r)
    }

    @Override
    public void closeProject(String s) throws Exception {

    }

    @Override
    public String[] getPluginSymbolicNames() {
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        return plugins.toList().collect {it.getName()}.toArray(new String[0])
//        return new String[0];
    }

    public boolean tryFindWithoutDollar = true

    PsiClass findClass(String clazz){
        Project project = getOpenedProject();
        ProjectAndLibrariesScope scope = new ProjectAndLibrariesScope(project);
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(clazz, scope);
        if (psiClass == null) {
            if(tryFindWithoutDollar){
                int dollar = clazz.indexOf('$')
                if(dollar>0){
                    String replaced = clazz.replace('$','.')
                    psiClass = JavaPsiFacade.getInstance(project).findClass(replaced, scope);
                    if(psiClass!=null){
                        return psiClass
                    }

                    String originClass = clazz.substring(0,dollar)
                    psiClass = JavaPsiFacade.getInstance(project).findClass(originClass, scope);
                    if(psiClass!=null){
                        return psiClass
                    }
                }
            }
            throw new ClassNotFoundException(clazz);
        }
        return psiClass
    }

    @Override
    public void openClass(String clazz) throws Exception {
        invokeAndWaitInSwingThread {
            Project project = getOpenedProject();
            PsiClass psiClass = findClass(clazz);
            VirtualFile virtualFile = psiClass.getContainingFile().getViewProvider().getVirtualFile();
            SwingUtilities.invokeLater {
                FileEditor[] fileEditors = FileEditorManager.getInstance(project).openFile(virtualFile, true);
            }
        }
    }


    @Override
    public void openMethod(String clazz, String methodName, int paramsCount) throws Exception {
        invokeAndWaitInSwingThread {

            Project project = getOpenedProject();
            ProjectAndLibrariesScope scope = new ProjectAndLibrariesScope(project);
            PsiClass psiClass = findClass(clazz);
            PsiMethod[] allMethods = psiClass.getAllMethods();
            for (PsiMethod psiMethod :                    allMethods) {
                if (methodName.equals(psiMethod.getName()) && psiMethod.getParameterList().getParametersCount() == paramsCount) {

                    psiMethod.navigate(true);
                    return;
                }
            }
            throw new NoSuchMethodException(clazz + " "
                    + methodName + "(" + paramsCount + ")");
        }

    }

    @Override
    public void showStackTrace(String s) throws Exception {
        Project project = getOpenedProject();
        SwingUtilities.invokeLater {
            AnalyzeStacktraceUtil.addConsole(project,
                    null, "<Stacktrace p>", s)
        }

    }

    static boolean isAtLeastOneProjectOpened() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects()
        return openProjects != null && openProjects.length > 0
    }

    static Project getOpenedProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects == null || openProjects.length == 0) {
            throw new IllegalStateException("Can't find open project");
        }
        return openProjects[0];

    }


    @Override
    public void openField(String clazz, String fieldName) throws Exception {
        Project project = getOpenedProject();
        ProjectAndLibrariesScope scope = new ProjectAndLibrariesScope(project);
        invokeAndWaitInSwingThread {
            PsiClass psiClass = findClass(clazz);
            PsiField allMethods = psiClass.findFieldByName(fieldName, true);
            if (allMethods == null) {
                throw new NoSuchFieldException(clazz + " "
                        + fieldName);
            }

            allMethods.navigate(true);
        }


    }

    @Override
    public void openConstructor(String clazz, int paramsCount) throws Exception {
        invokeAndWaitInSwingThread {
            Project project = getOpenedProject();
            ProjectAndLibrariesScope scope = new ProjectAndLibrariesScope(project);
            PsiClass psiClass = findClass(clazz);
            PsiMethod[] allMethods = psiClass.getAllMethods();
            for (PsiMethod psiMethod : allMethods) {
                if (psiMethod.getParameterList().getParametersCount() == paramsCount) {

                    psiMethod.navigate(true);
                    return;
                }
            }
            throw new NoSuchMethodException(clazz + " "
                    + "(" + paramsCount + ")");
        }
    }

    @Override
    public void importProject(File file) throws Exception {

    }

    @Override
    public void saveWorkspace() throws Exception {

    }

    @Override
    public void openProject(String s) throws Exception {

    }

    @Override
    public void setMonitorOutFile(File file) throws FileNotFoundException {

    }

    @Override
    public void saveAllAndClose() throws Exception {
        PsiDocumentManager.getInstance(openedProject).commitAllDocuments();
        ApplicationManager.application.exit()
    }

    @Override
    public void refreshAndCleanWorkspace() throws Exception {

    }

    @Override
    public void disablePlugin(String s) throws Exception {
        com.intellij.ide.plugins.PluginManagerCore.disablePlugin(s);
    }

    @Override
    public void runLaunchConfiguration(String launchName, String mode) throws Exception {
        if (mode == null) {
            mode = "Run";
        }
        Executor executor = ExecutorRegistry.getInstance().getExecutorById(mode);
        if (executor == null) {
            throw new IllegalArgumentException("Executor not found : " + mode);
        }
        RunManagerEx runManager = RunManagerEx.getInstanceEx(OSIntegrationIdea.getOpenedProject());
        RunnerAndConfigurationSettings configurationByName = runManager.findConfigurationByName(launchName);

        if (configurationByName == null) {
            throw new IllegalArgumentException("Configuration not found : " + launchName);
        }
        Runnable r = {ExecutionUtil.runConfiguration(configurationByName, executor);}
        JrrIdeaUtils.submitTr(r)
    }

    static VirtualFile conevertFileToVirtual(File file){
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        return virtualFile;
    }


    @Override
    public void openFile(File file, String s) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new FileNotFoundException("Not a file : " + file.getAbsolutePath());
        }
        file = file.canonicalFile.absoluteFile
        VirtualFile virtualFile = findVirtualFile(file)

        Project project = OSIntegrationIdea.getOpenedProject();
        invokeAndWaitInSwingThread {
            OpenFileAction.openFile(virtualFile, project);
        }
    }


    //com.intellij.psi.JavaPsiFacade.findPackage

    static VirtualFile findVirtualFile(File file){
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (virtualFile == null) {
            throw new Exception("virtual file is null for ${file}");
        }
        if (!virtualFile.isValid()) {
            throw new Exception("virtual file not valid for ${file}");
        }
        return virtualFile
    }

    @Override
    public void openFileAndSelectInRse(File file, String s) throws Exception {
        openFile(file, s);
    }


    static com.intellij.util.indexing.FileBasedIndex getFileBasedIndex(){
        return com.intellij.util.indexing.FileBasedIndex.getInstance()
    }
}
