package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class BuildCacheExpireTime {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static int getBuildCacheExpireTimeInDays(){
        return GradleEnvsUnsafe.gradle.getSettings().getBuildCache().getLocal().getRemoveUnusedEntriesAfterDays()
    }

    static void setBuildCacheExpireTimeInDays(int days){
        GradleEnvsUnsafe.gradle.getSettings().getBuildCache().getLocal().setRemoveUnusedEntriesAfterDays(days)
        JrrClassUtils.setFieldValue(org.gradle.cache.internal.LeastRecentlyUsedCacheCleanup,'DEFAULT_MAX_AGE_IN_DAYS_FOR_EXTERNAL_CACHE_ENTRIES', days)
    }

}
