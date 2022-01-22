package net.sf.jremoterun.utilities.nonjdk.store.complexwriters;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class RecentFilesStore<T> extends ListStore2<T> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<T> currentList
    public File storeFile
    public static int maxFilesToStoreS = 5
    public int maxFilesToStore = maxFilesToStoreS

    RecentFilesStore(File file) {
        storeFile = file
        if(storeFile.exists()) {
            currentList = loadSettingsS(storeFile)
        }else {
            currentList = []
        }
    }

    void saveList6() {
        int last = Math.min(currentList.size(), maxFilesToStore)
        List<T> subList = currentList.subList(0, last)
        storeFile.text = saveComplexObject(subList)
    }


}
