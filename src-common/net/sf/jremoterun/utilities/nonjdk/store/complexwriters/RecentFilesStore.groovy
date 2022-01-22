package net.sf.jremoterun.utilities.nonjdk.store.complexwriters;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class RecentFilesStore extends ListStore2<File> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<File> currentList
    public File storeFile
    public static int maxFilesToStoreS = 5

    RecentFilesStore(File file) {
        super()
        storeFile = file
        if(storeFile.exists()) {
            currentList = loadSettingsS(storeFile)
        }else {
            currentList = []
        }
    }

    void saveList6() {
        int last = Math.min(currentList.size(), maxFilesToStoreS)
        List<File> subList = currentList.subList(0, last)
        storeFile.text = saveComplexObject(subList)
    }


}
