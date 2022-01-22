package net.sf.jremoterun.utilities.nonjdk.idea.utils

import com.intellij.util.lang.ClassPath
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClassPathStoreImpl

import java.nio.file.Path;
import java.util.logging.Logger;

@CompileStatic
class IdeaClasspathUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static List<File> getClassPathBase() {
        ClassPath classPath = IdeaClassPathStoreImpl.findClassPath1()
        return classPath.getBaseUrls().collect { it.toFile() }
    }

    static List<File> getClassFilesForThisPlugin() {
        ClassPath classPath = IdeaClassPathStoreImpl.findClassPath1()
        return getClassFiles2(classPath)
    }

    static List<File> getClassFiles2(ClassPath classPath ) {
        Path[] files = JrrClassUtils.getFieldValue(classPath, 'files') as Path[]
        if (files == null) {
            return null
        }
        return files.toList().collect { it.toFile() }
    }


}
