package net.sf.jremoterun.utilities.nonjdk.props.propstrack;

import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.*;
import java.util.logging.Logger;

public class PropsTracker {

    public static String propsRandomName = JrrClassUtils.getCurrentClass().getName() + ".propsRandomName";

    public static boolean copyProps = true;
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static PropsTracker propsTrackerInstance;
    public PropertiesTracker propertiesTracker;
    public Map<Object, LocationElement> usedProps = new HashMap<>();
    public volatile boolean logStack = false;
    public Set<Object> logStackAllThis = new HashSet<>();
    public volatile boolean doMonitor = true;
    public Date creationDate = new Date();
    public Properties propsBefore;
    private LocationElement notUsed1 = new LocationElement();
    private JustStackTrace2 notUsed2 = new JustStackTrace2();

    public PropsTracker() {
    }

    public static PropsTracker sett() {
        Properties oldProps = System.getProperties();
        if (oldProps instanceof PropertiesTracker) {
            PropertiesTracker props = (PropertiesTracker) oldProps;
            log.info("prop tracker already set ");
            return props.receivePropsTracker();
        }
        PropsTracker propsTracker = new PropsTracker();
        PropertiesTracker propertiesTracker2;
        if(copyProps) {
            propertiesTracker2 = new PropertiesTracker(propsTracker);
            for (Map.Entry e : System.getProperties().entrySet()) {
                propertiesTracker2.put(e.getKey(), e.getValue());
            }
        }else{
            propertiesTracker2 = new PropertiesTracker(propsTracker,System.getProperties());
        }
        propertiesTracker2.getProperty(propsRandomName);
        PropsTracker propertiesTracker = settImpl(propertiesTracker2);
        propsTrackerInstance = propertiesTracker;
        Properties newProps = System.getProperties();
        if (newProps.getClass() != PropertiesTracker.class) {
            throw new IllegalStateException("Strange prop class : " + newProps.getClass().getName());
        }
        return propertiesTracker;
    }

    public static PropsTracker settImpl(PropertiesTracker propertiesTracker) {
        Properties oldProps = System.getProperties();
        System.setProperties(propertiesTracker);
        propertiesTracker.propsTracker.propsBefore = oldProps;
        return propertiesTracker.receivePropsTracker();
    }


    void doTrack(Object key, Object value2, boolean removed) {
        if (doMonitor) {
            synchronized (propertiesTracker) {
                LocationElement le = usedProps.get(key);
                boolean needTrack = false;
                if (le == null) {
                    needTrack = true;
                    le = new LocationElement();
                }else{
                    if(!needTrack && !le.propRemoved){
                        needTrack = true;
                    }
                }
                boolean logAllStack2 = false;
                if (logStackAllThis.size() > 0) {
                    logAllStack2 = logStackAllThis.contains(key);
                    if (logAllStack2) {
                        needTrack = true;
                    }
                }

                if (needTrack) {
                    usedProps.put(key, le);
                    le.isUsed = value2 != null;
                    if(removed && !le.propRemoved){
                        le.propRemoved = true;
                    }
                    if (value2!=null && !(value2 instanceof String)) {
                        le.propValueClass  = value2.getClass();
                    }
                    if (logStack) {
                        le.fullLocation = new JustStackTrace2();
                    }
                    if (logAllStack2) {
                        le.locationAll.add(new JustStackTrace2());
                    }
                }
            }
        }
    }

//    boolean isNeedTrack(LocationElement le, Object key) {
//        return false;
//    }

    void doTrackImpl(LocationElement le, Object key, boolean isUsed) {
    }

}
