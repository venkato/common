package nik.git.forcepush.jrr;

import java.util.logging.Logger;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.git.GitRemoteFind;

public class GitRemoteFindEclipse extends GitRemoteFind{

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	public String startsWithaa;
	
	
	
	public GitRemoteFindEclipse(String startsWithaa) {
		super();
		this.startsWithaa = startsWithaa;
	}



	@Override
	public boolean isRepoGood(String uri) {
		return uri.startsWith(startsWithaa);
	}

}
