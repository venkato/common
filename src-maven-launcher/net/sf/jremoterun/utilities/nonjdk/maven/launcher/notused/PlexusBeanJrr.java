package net.sf.jremoterun.utilities.nonjdk.maven.launcher.notused;

import net.sf.jremoterun.utilities.classpath.ClRef;
import org.eclipse.sisu.plexus.PlexusBean;

public class PlexusBeanJrr implements PlexusBean {

    //public ClRef impl1 ;
    public Class clazzz ;
    public Object value1;

    public PlexusBeanJrr(Class clazzz) {
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
        if(value1==null){
            try {
                value1 = clazzz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return value1;
    }

    @Override
    public Object setValue(Object value) {
        throw new RuntimeException("unsupported "+value);
    }
}
