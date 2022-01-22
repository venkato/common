package net.sf.jremoterun.utilities.nonjdk.git.lab

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gitlab4j.api.models.MergeRequest;

import java.util.logging.Logger;

@CompileStatic
class GitlabMrMonitorHelper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitlabMrMonitor gitlabMrMonitor;

    GitlabMrMonitorHelper(GitlabMrMonitor gitlabMrMonitor) {
        this.gitlabMrMonitor = gitlabMrMonitor
    }


    void onList(List<MergeRequest> mergeRequests) {
        //List<Long> mrIds = []
        for (MergeRequest request1 : mergeRequests){
            gitlabMrMonitor. gotMergeRequest(request1)
            //mrIds.add(it.getIid())
        }
        //log.info "got mr = ${mrIds}"
        for (MergeRequest request2 : mergeRequests){
            gitlabMrMonitor.gotMergeRequest2(request2)
        }
    }

}
