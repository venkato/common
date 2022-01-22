package net.sf.jremoterun.utilities.nonjdk.props.propstrack;

import java.util.Properties;

public class PropertiesTracker extends Properties {


    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public PropsTracker propsTracker;

    public PropertiesTracker(PropsTracker propsTracker ) {
        this.propsTracker = propsTracker;
        propsTracker.propertiesTracker = this;
    }

    public PropertiesTracker(PropsTracker propsTracker,Properties var1 ) {
        super(var1);
        this.propsTracker = propsTracker;
        propsTracker.propertiesTracker = this;
    }

    @Override
    public String getProperty(final String key) {
        String ret = super.getProperty(key);
        propsTracker.doTrack(key, ret,false);
        return ret;
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        boolean removed=  super.remove(key, value);
        if(removed){
            propsTracker.doTrack(key,value,true);
        }
        return removed;
    }

    @Override
    public synchronized Object remove(Object key) {
        return super.remove(key);
    }

    @Override
    public synchronized Object get(Object key) {
        Object ret = super.get(key);
        propsTracker.doTrack(key, ret,false);
        return ret;
    }



    public PropsTracker receivePropsTracker() {
        return propsTracker;
    }

}
