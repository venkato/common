package net.sf.jremoterun.utilities.nonjdk.maven.http;

import org.eclipse.sisu.plexus.PlexusBean;

public class PlexusBeanPerLookupJrr implements PlexusBean {

    //public ClRef impl1 ;
    public Class clazzz;
//    public Object value1;

    public PlexusBeanPerLookupJrr(Class clazzz) {
        this.clazzz = clazzz;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Class getImplementationClass() {
        return clazzz;
    }

    @Override
    public Object getKey() {
        throw new RuntimeException("unsupported ");
        //return null;
    }

    @Override
    public Object getValue() {

        try {
            return clazzz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Object setValue(Object value) {
        throw new RuntimeException("unsupported " + value);
    }
}
