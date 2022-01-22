package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.PrimiteClassesUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.JavaObjectFieldsDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.ArrayDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.CollectionDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.DateWithFormatDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.DirectDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.JavaFieldDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.JavaObjectProps2Dumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.MapDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.ToStringObjectDumper

import java.lang.reflect.Array
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * @see org.commonutils.CommonToString2
 */
@CompileStatic
class ObjectDumperProvider {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ObjectDumperI defaultDumper;

    public Map<String, ObjectDumperI> customDumpers = [:]

    public boolean writeNullO = true
    public boolean sortList = true
    public ObjectDumperI primitiveDumper = new ToStringObjectDumper();
    public ObjectDumperI enumDumper = new ToStringObjectDumper();
    public ObjectDumperI stringDumper = new ToStringObjectDumper();
    public ObjectDumperI numberDumper = new ToStringObjectDumper();
    public ObjectDumperI arrayDumper = new ArrayDumper();
    public ObjectDumperI mapDumper = new MapDumper();
    public ObjectDumperI dateDumper = new DateWithFormatDumper();
    public ObjectDumperI collectionDumper = new CollectionDumper();
    public ObjectDumperI directDumper = new DirectDumper();
    public IdentityHashMap<Object, Object> dumpedObjects = new IdentityHashMap<>()
    public int maxObjectToDump = 1000

    public Object emptyArray;
    public Object emptyMap;
    public Object nullObject;
    //public HashSet<Class> directs = [Long,Integer,Short,Boolean,Byte,]

    ObjectDumperProvider(ObjectDumperI defaultDumper) {
        this.defaultDumper = defaultDumper
        init()
    }

    ObjectDumperProvider(boolean fields) {
        if (fields) {
            defaultDumper = new JavaObjectFieldsDumper();
        } else {
            defaultDumper = new JavaObjectProps2Dumper()
        }
        init()
    }

    void init(){
        addCustomDumper(Field, new JavaFieldDumper())
    }

    <T> void addCustomDumper(Class<T> clazz,ObjectDumperI<T> objectDumperI){
        customDumpers.put(clazz.getName(),objectDumperI)
    }

    ObjectDumperI getObjectDumper(Class ooo) {
        ObjectDumperI custom1 = customDumpers.get(ooo.getName())
        if (custom1 != null) {
            return custom1
        }
        if (ooo.isArray()) {
            return arrayDumper
        }
        if (ooo.isPrimitive()) {
            return directDumper
        }
        if (ooo.isEnum()) {
            return enumDumper
        }
        if (Collection.isAssignableFrom(ooo)) {
            return collectionDumper;
        }
        if (Map.isAssignableFrom(ooo)) {
            return mapDumper;
        }
        if (Number.isAssignableFrom(ooo)) {
            return numberDumper;
        }
        if (Date.isAssignableFrom(ooo)) {
            return dateDumper;
        }
        if (ooo == String) {
            return stringDumper
        }
        if (PrimiteClassesUtils.primitiveWrapperClasses.contains(ooo)) {
            return directDumper;
        }
        return defaultDumper
    }

    private final Object sampleObject = new Object()

    Object dumpObject(Object ooo) {
        if (ooo == null) {
            return nullObject
        }
        final Class clazz1 = ooo.getClass()
        if (clazz1.isArray()) {
            int length1 = Array.getLength(ooo)
            if (length1 == 0) {
                return emptyArray
            }
        }
        if (isCount(clazz1)) {
            if (dumpedObjects.get(ooo) != null) {
                return alreadyDumped(ooo)
            }

            dumpedObjects.put(ooo, sampleObject)
            if (dumpedObjects.size() > maxObjectToDump) {
                //throw new IllegalStateException("Too many objects to dump ${dumpedObjects.size()} at ${aa}")
                onTooManyObjects(ooo)
            }
        }
        try {
            ObjectDumperI dumper = getObjectDumper(clazz1)

            return dumper.dumpObject(ooo, this)
        } catch (Throwable e) {
            final String aa = clazz1.getName();//+'@'+Integer.toHexString(ooo.hashCode())
            log.info "failed dump ${aa}"
            throw e
        }
    }

    String alreadyDumped(Object ooo) {
        final String aa = ooo.getClass().getName()+'@'+Integer.toHexString(ooo.hashCode())
        return "Already dumped ${aa}"
    }

    void onTooManyObjects(Object ooo) {
        throw new IllegalStateException("Too many objects to dump ${dumpedObjects.size()} at ${ooo.getClass().getName()}")
    }

    boolean isCount(Class clazz) {
        if (clazz.isPrimitive()) {
            return false
        }
        if (clazz == String) {
            return false
        }
        if (Date.isAssignableFrom(clazz)) {
            return false
        }
        if (Number.isAssignableFrom(clazz)) {
            return false
        }
        if (clazz.isEnum()) {
            return false
        }

        if (PrimiteClassesUtils.primitiveWrapperClasses.contains(clazz)) {
            return false
        }
        return true
    }


}
