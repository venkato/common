package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator;

import net.sf.jremoterun.utilities.JrrClassUtils

import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ListStore2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.RecentFilesStore;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class FileStore extends RecentFilesStore {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    FileStore(File file) {
        super(file)
    }
}
