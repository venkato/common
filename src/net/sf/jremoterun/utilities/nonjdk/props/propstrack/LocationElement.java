package net.sf.jremoterun.utilities.nonjdk.props.propstrack;


import java.util.LinkedList;
import java.util.List;

public class LocationElement {

    // TODO
    // track all locations
    // prop name not string,
    // prop value not string
    // track removing

    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public StackTraceElement location;
    public List<JustStackTrace2> locationAll = new LinkedList<>();
    public JustStackTrace2 fullLocation;
    public boolean isUsed ;
    public Class propValueClass ;
    public boolean propRemoved = false;


    @Override
    public String toString() {
        return String.valueOf(location);
    }

}
