package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IndexReadyListener
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.*
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.mdep.ivy.IvyListenerManager
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl

import javax.swing.*
import java.awt.*
import java.util.List
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public abstract class LibManager33 {


    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());


    public static boolean tryDownloadSourcesDefault = true
    public boolean tryDownloadSources = tryDownloadSourcesDefault

    //public IdeaLibManagerSwing dialog8;
//    Library library;
//    private TextFieldWithHistory txtDirectoryToSearch;

    LibManager33() {
        //this.dialog8 = dialog8
    }

//    void addFileToLib(File fileToSave) {
//        assert fileToSave.parentFile.exists()
//        dialog8.txtDirectoryToSearch.addCurrentTextToHistory()
//        dialog8.listStore.currentList.add(0, fileToSave)
//        dialog8.listStore.currentList = dialog8.listStore.currentList.unique()
//        dialog8.listStore.saveList6()
//    }

    void addSources(LibItem libItem, NewValueListener<List<MavenId>> valueListener) {
        NewValueListener valueListener4 = valueListener
        try {
            SwingUtilities.invokeLater {
                Project project = OSIntegrationIdea.openedProject;
                Task task = new Task.Backgroundable(project, "Add sources ...", true) {
                    @Override
                    public void run(ProgressIndicator indicator) {
                        try {
                            Object result3 = addSources3(indicator, libItem)
                            if (valueListener4 != null) {
                                valueListener4.newValue(result3)
                            }
                        } catch (Throwable e) {
                            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed add source", e)
                        }

                    }
                };
                ProgressManager.getInstance().run(task);
            }

//        log.info "add sources fine ${fileToSave}"
        } catch (Throwable e) {
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed add source", e)
        }
    }

    List<MavenId> addSources3(ProgressIndicator progressIndicator, LibItem libItem) {
        IdeaClasspathLongTaskInfo longTaskInfo = new IdeaClasspathLongTaskInfo(progressIndicator)
        ManyReposDownloaderImpl resolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver as ManyReposDownloaderImpl
        IdeaIvyEvent ideaIvyEvent = new IdeaIvyEvent(longTaskInfo);
        resolver.addIvyListener(ideaIvyEvent)
        try {
            List<MavenId> result = addSources2(longTaskInfo, libItem);
            log.info("add source done for ${libItem.library.getName()}")
            return result;
        } finally {
            resolver.removeIvyListener(ideaIvyEvent)
        }

    }

    List<MavenId> addSources2(LongTaskInfo longTaskInfo, LibItem libItem) {
        // List<MavenId> noSourceInLocalSources = []
//        log.info "found maven sources libs : ${alreadedAddedSources2}"

        LibSaver4 ls = new LibSaver4(libItem.library, OrderRootType.CLASSES, longTaskInfo);
        ls.calcMavenCache()
//        ls.defaultMavenDepDownloader = CreateClassLoaderProxy.classLoaderProxy.createDropshipDependencyResolvedInSeparetClassloader3();
        List<MavenId> binWithMissingSources = ls.saveClassPathFromIdeaLib()


        LibSaver4 saver2 = new LibSaver4(libItem.library, OrderRootType.SOURCES, longTaskInfo);
        ls.copyCaches(saver2)
        List<MavenId> alreadedAddedSources2 = saver2.saveClassPathFromIdeaLib()

        log.info "found maven binaries libs : ${binWithMissingSources}"
        binWithMissingSources -= alreadedAddedSources2;
        log.info "found maven binaries without sources : ${binWithMissingSources}"

        MavenCommonUtils mavenCommonUtilsSrc = new MavenCommonUtils();
        mavenCommonUtilsSrc.fileType = MavenFileType2.source.fileSuffix

        binWithMissingSources = binWithMissingSources.sort()
        if (tryDownloadSources) {
            List<MavenId> allMissedSources = binWithMissingSources.findAll {
                mavenCommonUtilsSrc.findMavenOrGradle(it) == null
            }
            MavenDependenciesResolver resolver = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
            allMissedSources.each {
                longTaskInfo.setCurrentTask("${it} downloading source ..")
                resolver.resolveAndDownloadDeepDependencies(it, true, false)
            }

        }

        ls.noSourceInLocalSources.addAll(binWithMissingSources.findAll {
            mavenCommonUtilsSrc.findMavenOrGradle(it) == null
        })
        ls.noSourceInLocalSources.sort()
        log.info "no sources ${ls.noSourceInLocalSources}"


        binWithMissingSources.removeAll(ls.noSourceInLocalSources)
        if (binWithMissingSources.size() == 0) {
            SwingUtilities.invokeLater {
                String msg = ""
                if (ls.noSourceInLocalSources.size() > 0) {
                    msg = "No sources : ${ls.noSourceInLocalSources}, "
                }
                msg = "${msg}Can' add find more sources"
                Component component78 = getCOmponentToLinkCoordination()
                JOptionPane.showMessageDialog(component78, msg)
            }
        } else {
            String msg;
            if (ls.noSourceInLocalSources.size() > 0) {
                msg = "No sources : ${ls.noSourceInLocalSources}, "
            }
            addSources4(binWithMissingSources, libItem, msg)
//            }
        }
        return binWithMissingSources
    }

    abstract Component getCOmponentToLinkCoordination();


    void addSources4(List<MavenId> binWithMissingSources, LibItem libItem, String msg) {
        MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()
        mavenCommonUtils.fileType = MavenFileType2.source.fileSuffix
        List<File> collect = binWithMissingSources.collect { mavenCommonUtils.findMavenOrGradle(it) }
        log.info "ask to add : ${collect}"
        log.info "ask to add : ${binWithMissingSources}"

        Disposable disposable = new Disposable() {

            @Override
            void dispose() {
                log.info "action finished"
            }
        }

        Runnable r = {
            try {
                log.info "adding files"
                Library.ModifiableModel modifiableModel = libItem.library.getModifiableModel();
                LibConfigurator8 configurator8 = new LibConfigurator8(modifiableModel)
                configurator8.assertWriteActionAllowed()
                configurator8.model = modifiableModel
                collect.each { configurator8.addSourceFImpl(it) }
                configurator8.commit()
                log.info "commit done ${libItem.library.name}"
                log.info "${libItem.library.name} : files added ${collect.size()}"
            } catch (Throwable e) {
                log.info "${e}"
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Lib managed failed", e);
            }
        }

//            SwingUtilities.invokeLater {
        msg = "${msg}Add ? ${binWithMissingSources}"
        Component component = getCOmponentToLinkCoordination()
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(component, msg)) {
            // r.run();
            log.info "cp 444"
            LibConfigurator8.submitTr(r);
        }
    }

    static void addCustomAny(Library libItem, NewValueListener<LibConfigurator8> binWithMissingSources) {

        Runnable r = {
            try {
                log.info "adding files"
                Library.ModifiableModel modifiableModel = libItem.getModifiableModel();
                LibConfigurator8 configurator8 = new LibConfigurator8(modifiableModel)
                configurator8.assertWriteActionAllowed()
                configurator8.model = modifiableModel
                binWithMissingSources.newValue(configurator8)
                configurator8.commit()
                log.info "commit done ${libItem.getName()}"
            } catch (Throwable e) {
                log.info "${e}"
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Lib managed failed", e);
            }
        }

        LibConfigurator8.submitTr(r);
    }

    File checkFile(String fileToSaveOrig) {
        if (fileToSaveOrig == null) {
            log.info("error1")
            JOptionPane.showMessageDialog(null, "Select file null")
            return null
        }
        if (fileToSaveOrig == '') {
            log.info("error2")
            JOptionPane.showMessageDialog(null, "Select file empty")
            return null
        }

        fileToSaveOrig = fileToSaveOrig.trim()
        if (fileToSaveOrig == null) {
            log.info("error2")
            JOptionPane.showMessageDialog(null, "Select file")
            return null
        }
        File fileToSave = fileToSaveOrig as File
        if (fileToSave == null) {
            JOptionPane.showMessageDialog(null, "Select file")
            return null
        }
        if (fileToSave.getParentFile() == null) {
            JOptionPane.showMessageDialog(null, "Failed find parent for ${fileToSave}")
            return null
        }
        if (!fileToSave.parentFile.exists()) {
            JOptionPane.showMessageDialog(null, "Parent file not exist")
            return null
        }
        if (fileToSave.isDirectory()) {
            JOptionPane.showMessageDialog(null, "File is directory")
            return null
        }
//        if(!fileToSave.canRead()){
//            JOptionPane.showMessageDialog(null,"Can't read file")
//            return false
//        }
        return fileToSave
    }

    public void runImportImpl(LibItem libItem, File fileToSave) {
        try {
            assert fileToSave.exists()
            assert fileToSave.file
            assert fileToSave.canRead()
            if (!fileToSave.exists()) {
//                JOptionPane.showMessageDialog(null, "File not found")
//                return
                throw new FileNotFoundException(fileToSave.absolutePath)
            }

            IdeaAddFileWithSources withSources = IdeaAddFileWithSourcesFactory.defaultFactory.createAdded();
//        LibConfigurator8 configurator4 = new LibConfigurator8()
//        String libName = libItem.library.getName();
            Runnable r76 = {
                try {
                    log.info "creating modified lib object"
                    LibConfigurator8 configurator9 = fetch11(libItem)
                    log.info "deleting all libs .. "
                    configurator9.deleteAll()
                    configurator9.commit()
                    log.info "deleting all libs done "
                    Runnable r55 = {
                        log.info "doing import8"
                        LibConfigurator8 configurator8 = fetch11(libItem)
                        configurator8.assertWriteActionAllowed()
//                configurator8.prepare(libItem.library.getName())
                        configurator8.deleteAll();
                        withSources.binaries.each { configurator8.addLibraryWithSource(it, null) }
                        log.info("added source count : ${withSources.sources.size()}")
                        log.info("added files count : ${withSources.binaries.size()}")
                        withSources.sources.each { configurator8.addSourceF(it) }
                        withSources.sourcesString.each { configurator8.addSourceS(it) }
                        configurator8.assertWriteActionAllowed()
                        log.info "import fine ${fileToSave}"
                        configurator8.commit()

                        importFinishedFineUnsafe.run()
                        log.info "${libItem.library.getName()} commit done "
                    }

                    SwingUtilities.invokeLater {
                        LibConfigurator8.submitTr {
                            try {
                                r55.run()
                            } catch (Throwable e) {
                                importFailed.newValue(e)
                            }
                        }
                    }
                } catch (Throwable e) {
                    importFailed.newValue(e);
                }
            }
            Runnable readyCallback = {
                try {
                    SwingUtilities.invokeLater {
                        LibConfigurator8.submitTr(r76);
                    }
                } catch (Throwable e) {
                    importFailed.newValue(e)
                }
            }
            withSources.import2(OSIntegrationIdea.openedProject, fileToSave, readyCallback);
        } catch (Throwable e) {
            importFailed.newValue(e)
        }
    }

    public static Runnable importFinishedFineUnsafe = {
        Runnable r = {
            log.info "sleep for 1 sec .."
            Thread.sleep(1000);
            DumbService.getInstance(IndexReadyListener.getOpenedProject()).smartInvokeLater {
                log.info "index should be ready now"
                importFinishedFine.run()
            }
        }
        Thread thread = new Thread(r, 'Sleep after import classpath')
        thread.start()
    };

    public static volatile Runnable importFinishedFine = {};
    public static volatile NewValueListener<Throwable> importFailed = new NewValueListener<Throwable>() {
        @Override
        void newValue(Throwable throwable) {
            log.log(Level.SEVERE, "Failed import", throwable)
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed import", throwable);
        }
    };


    void importLib(String libName, Project global, File f) {
        runImportImpl(new LibItem(findLib(libName, global), global), f)
    }

    Library findLib(String libName, Project global) {
        LibraryTablesRegistrar registrar = LibraryTablesRegistrar.getInstance();
        List<Library> libraries;
        if (global == null) {
            LibraryTable libraryTable = registrar.getLibraryTable();
            libraries = libraryTable.getLibraries().toList();
        } else {
            LibraryTable libraryTable = registrar.getLibraryTable(global);
            libraries = libraryTable.getLibraries().toList();
        }
        if (libraries.size() == 0) {
            throw new Exception("not lib found : ${global == null}")
        }

        List<Library> find2 = libraries.findAll { it.getName() == libName }
        if (find2.size() == 0) {
            throw new Exception("Not found ${libName}, existed : ${libraries.collect { it.getName() }.sort()}")
        }
        if (find2.size() > 1) {
            throw new Exception("found many with same name : ${find2.size()}")
        }
        return find2[0]
    }

    LibConfigurator8 fetch11(LibItem libItem) {
//        List<Library> list1 = LibraryTablesRegistrar.getInstance().getLibraryTable().getLibraries().toList()
//        List<Library> matchedLib3 = list1.findAll { it.getName() == libItem.library.getName() }
//        assert matchedLib3.size() ==1
//        return new LibConfigurator8(matchedLib3[0].getModifiableModel());
        return new LibConfigurator8(findLib(libItem.library.getName(), libItem.global).getModifiableModel())
    }


    public void runExportImpl(LibItem libItem, File fileToSave) {

        SwingUtilities.invokeLater {
            Project project = OSIntegrationIdea.openedProject;
            Task task = new Task.Backgroundable(project, "Export library ...", true) {
                @Override
                public void run(ProgressIndicator indicator) {
                    runExport2(indicator, fileToSave, libItem)
                }
            };
            ProgressManager.getInstance().run(task);
        }
        log.info "export fine ${fileToSave}"
    }


    void runExport2(ProgressIndicator progressIndicator, File fileToSave, LibItem libItem) {
        IdeaClasspathLongTaskInfo longTaskInfo = new IdeaClasspathLongTaskInfo(progressIndicator)
        MavenDependenciesResolver resolver1 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
        IvyListenerManager resolver

        if (resolver1 instanceof IvyListenerManager) {
            resolver = resolver1 as IvyListenerManager
        }

        IdeaIvyEvent ideaIvyEvent = new IdeaIvyEvent(longTaskInfo);
        if (resolver != null) {
            resolver.addIvyListener(ideaIvyEvent)
        }
        try {
            LibSaver saver = new LibSaver()
            saver.saveClassPathFromIdeaLibToFile(libItem.library, fileToSave, longTaskInfo);
            log.info("export llib fine to ${fileToSave}")
        } finally {
            if (resolver != null) {
                resolver.removeIvyListener(ideaIvyEvent)
            }
        }

    }

}
