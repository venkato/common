package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.title.DockingWindowTitleProvider;


@CompileStatic
public class MyDockingWindowTitleProvider implements DockingWindowTitleProvider {

	private String title;
	
	
	
	public MyDockingWindowTitleProvider(String title) {
		this.title = title;
	}

	@Override
	public String getTitle(DockingWindow dockingwindow) {
		return title;
	}
}
