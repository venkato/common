package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.transport.FetchResult;

import java.util.logging.Logger;

@CompileStatic
class GitFetcherIfNeeded {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitRepoUtils gitRepoUtils;
    public boolean fetch= false;
    public List<String> fetchPrefix = ['origin',]
    public List<String> removeAfter = ['~','^',':',]

    GitFetcherIfNeeded(GitRepoUtils gitRepoUtils) {
        this.gitRepoUtils = gitRepoUtils
    }

    FetchResult fetchIfNeeded(String commitID) {
        if(!fetch){
            return null
        }

        removeAfter.each {
            int iii = commitID.indexOf(it)
            if(iii>0){
                commitID =commitID.substring(0,iii)
            }
        }
        List<String> tokenize3 = commitID.tokenize('/')
        if (tokenize3.size() > 1) {
            String remoteName1 = fetchPrefix.find { tokenize3[0] == it }
            if(remoteName1!=null) {
                List<String> list3 = tokenize3.subList(1, tokenize3.size())
                log.info "fetching ${commitID}"
                FetchResult fetch2 = gitRepoUtils.fetch2(remoteName1, list3.join('/'))
                return fetch2
            }
        }
        return null
    }

}
