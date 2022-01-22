package net.sf.jremoterun.utilities.nonjdk.gi2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils

import java.util.logging.Logger

@CompileStatic
public class GitRepoUpdater {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	
	static  void fetchOneRepo(File repo) throws Exception {
		log.info("start fetch ${repo}");
		GitRepoUtils gitRepoUtils = new GitRepoUtils(repo)
		gitRepoUtils.fetchAllRemote()

	}
	
}
