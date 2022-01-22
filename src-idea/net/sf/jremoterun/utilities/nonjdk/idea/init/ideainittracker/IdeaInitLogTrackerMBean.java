package net.sf.jremoterun.utilities.nonjdk.idea.init.ideainittracker;

import groovy.transform.CompileStatic;

import java.util.Vector;

@CompileStatic
public interface IdeaInitLogTrackerMBean {

    Vector<IdeaLogItem> getListItems();

    String getParticularLogItemStringWithException(int i);

    String getParticularLogItemString(int i);

    IdeaLogItem getParticularLogItem(int i);

    IdeaLogItem getLastLogItem();

}