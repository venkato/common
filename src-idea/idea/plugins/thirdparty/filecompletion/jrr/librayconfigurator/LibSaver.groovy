package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.*
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ClassPathJrrStore

import java.util.logging.Logger

@CompileStatic
class LibSaver {

    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    public ClassPathJrrStore calculatorGroovy = new ClassPathJrrStore();

    LibSaver() {
    }

    void saveAllGlobalLibToDir(File dir, LongTaskInfo longTaskInfo) {
        assert dir.exists()
        LibraryTablesRegistrar registrar = LibraryTablesRegistrar.getInstance();
        LibraryTable libraryTable = registrar.getLibraryTable();
        libraryTable.getLibraries().toList().each {
            File libFile = new File(dir, it.name + ClassNameSuffixes.dotgroovy.customName)
            saveClassPathFromIdeaLibToFile(it, libFile, longTaskInfo)
        }
    }

    String saveClassPathFromIdeaLibToFile(Library library, File outfile, LongTaskInfo longTaskInfo) throws Exception {
        outfile.text = saveClassPathFromIdeaLib(library, longTaskInfo)
    }


    String saveClassPathFromIdeaLib(Library library, LongTaskInfo longTaskInfo) throws Exception {
        LibSaver4 saver2 = new LibSaver4(library, OrderRootType.SOURCES, longTaskInfo);
        saver2.calcMavenCache()
        saver2.saveClassPathFromIdeaLib2()
        saver2.classPathCalculator.calcClassPathFromFiles12()
        List filesSources = saver2.classPathCalculator.filesAndMavenIds
        filesSources = filesSources.findAll { !(it instanceof MavenId) }
        LibSaver4 saver3 = new LibSaver4(library, OrderRootType.CLASSES, longTaskInfo);
        saver2.copyCaches(saver3)
        List filesBinary = saver3.saveClassPathFromIdeaLib4()
        String classpath2 = saveClassPath7(filesBinary, filesSources)
        return classpath2
    }

    String saveClassPath7(List files, List sources) throws Exception {
        assert files != null
        return calculatorGroovy.saveClassPathWithSources(files, sources)
    }


}
