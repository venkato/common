package net.sf.jremoterun.utilities.nonjdk.classpath.java11;

import java.io.ObjectInputFilter;

public class ObjectInputFilterAllowAll implements java.io.ObjectInputFilter, Runnable{

    public ObjectInputFilter serialFilterBefore;


    @Override
    public ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo filterInfo) {
        return ObjectInputFilter.Status.ALLOWED;
    }


    public void saveValueBefore(){
        serialFilterBefore = Config.getSerialFilter();
    }

    public static void selfSet(){
        ObjectInputFilterAllowAll objectInputFilterAllowAll = new ObjectInputFilterAllowAll();
        objectInputFilterAllowAll.saveValueBefore();
        Config.setSerialFilter(objectInputFilterAllowAll);
    }

    @Override
    public void run() {
        selfSet();
    }
}
