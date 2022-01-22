package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class PropsDupsChecker  extends Properties {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    PropsDupsChecker() {
    }

    PropsDupsChecker(Properties var1) {
        super(var1)
    }

    @Override
    Object setProperty(String propertyName1, String newValue) {
        if(propertyName1 ==null){
            throw new NullPointerException('prop name is null')
        }
        if(newValue ==null){
            throw new NullPointerException('prop value is null')
        }
        String oldValue = getProperty(propertyName1)
        if(oldValue!=null){
            if(oldValue == newValue){
                throw new RuntimeException("Property = ${propertyName1} already added with same value = ${newValue}")
            }else{
                throw new RuntimeException("Property = ${propertyName1} already added : old value = ${oldValue} , new value = ${newValue}")
            }
        }
        return super.setProperty(propertyName1, newValue)
    }
}
