package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@CompileStatic
class ClassSlashNoSuffix  implements Comparable<ClassSlashNoSuffix> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String clName

    ClassSlashNoSuffix(String clName) {
        this.clName = clName
        assert !clName.endsWith(ClassNameSuffixes.dotclass.customName)
        assert !clName.contains('.')
    }

    boolean equals(object) {
        if (this.is(object)) return true
        if (!(object instanceof ClassSlashNoSuffix)) return false

        ClassSlashNoSuffix that = (ClassSlashNoSuffix) object

        if (clName != that.clName) return false

        return true
    }

    int hashCode() {
        return (clName != null ? clName.hashCode() : 0)
    }

    @Override
    int compareTo(@NotNull ClassSlashNoSuffix o) {
        if (o == null) {
            return 1
        }
        return clName.compareTo(o.clName)
    }


    @Override
    String toString() {
        return clName
    }
}


