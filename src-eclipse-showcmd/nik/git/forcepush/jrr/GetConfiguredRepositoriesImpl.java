package nik.git.forcepush.jrr;


import java.util.List;

import org.eclipse.egit.core.Activator;
import org.eclipse.jgit.lib.Repository;

import nik.git.forcepush.GetConfiguredRepositories;

public class GetConfiguredRepositoriesImpl extends GetConfiguredRepositories{

	@Override
	public List<java.io.File> getConfiguredRepositories() {
		Repository[] allRepositories = org.eclipse.egit.core.RepositoryCache.INSTANCE.getAllRepositories();
		List<java.io.File> repos=new java.util.ArrayList();
		for (Repository repository : allRepositories) {
			repos.add(repository.getDirectory());
		}
		
		//RepositoryCache
		//return Activator.getDefault().getRepositoryUtil().getConfiguredRepositories();
		return repos;
	}

}
