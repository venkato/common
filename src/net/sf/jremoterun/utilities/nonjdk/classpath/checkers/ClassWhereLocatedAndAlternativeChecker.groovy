package net.sf.jremoterun.utilities.nonjdk.classpath.checkers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.JrrZipUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternative
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternativeRef;

import java.util.logging.Logger;

@CompileStatic
class ClassWhereLocatedAndAlternativeChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void checkAll(Collection<ClassWhereLocatedAndAlternativeRef> refs) {
        refs.each {
            try {
                check(it.getRef())
            } catch (Exception e) {
                log.info "failed check ${it}"
                throw e;
            }

        }
    }

    static void check(ClassWhereLocatedAndAlternative ref) {
        String elToCheck1 = ref.clazz.getName().replace('.', '/') + ClassNameSuffixes.dotclass.customName
        String elToCheck2 = ref.clazz.getName().replace('.', '/') + ClassNameSuffixes.dotgroovy.customName
        File file = ref.alternative.resolveToFile()
        if (file.isFile()) {
            List<String> entries = JrrZipUtils.listEntries(file)
            if (entries.contains(elToCheck1) || entries.contains(elToCheck2)) {

            } else {
                throw new Exception("Class not found ${ref.clazz.getName()} in ${file.getAbsolutePath()}")
            }
        } else {
            if (new File(file, elToCheck1).exists() || new File(file, elToCheck2).exists()) {

            } else {
                throw new Exception("Class not found ${ref.clazz.getName()} in ${file.getAbsolutePath()}")
            }
        }
    }

}
