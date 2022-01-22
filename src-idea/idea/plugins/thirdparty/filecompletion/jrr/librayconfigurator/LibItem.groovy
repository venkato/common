package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.Library;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class LibItem implements Comparable<LibItem>{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Library library;
    public Project global

    LibItem(Library library, Project global) {
        this.library = library
        this.global = global
    }

    @Override
    String toString() {
        return library.getName()
    }

    @Override
    int compareTo( LibItem o) {
        return this.library.getName().compareTo(o.library.getName())
    }


}
