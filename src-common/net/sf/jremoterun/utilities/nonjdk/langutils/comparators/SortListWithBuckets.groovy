package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.reflect.Field;
import java.util.logging.Logger;

@CompileStatic
class SortListWithBuckets {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<Field> bucketsKeys
    public List list
    public ListComparator objectKeyComparator = new ListComparator()
    public ObjectFieldComparator objectFieldComparator = new ObjectFieldComparator()
    public Map<List, List> buckets = [:]

    SortListWithBuckets(List<Field> bucketsKeys, List list) {
        this.bucketsKeys = bucketsKeys
        this.list = list
    }

    List createKey(Object obj) {
        return bucketsKeys.collect {
            it.get(obj)
        }
    }

    List doJob() {
        createBuckets()
        sortBuckets()
        buckets.entrySet().each {
            try {
                sortWithinBuckets(it.key, it.value)
            } catch (Throwable e) {
                onException(it.key, it.value, e)
            }
        }
        return buildResult()
    }


    void createBuckets() {
        list.each {
            Object key1 = createKey(it)
            List get1 = buckets.get(key1)
            if (get1 == null) {
                get1 = []
                buckets.put(key1, get1)
            }
            get1.add(it)
        }
    }

    List buildResult() {
        List result = []
        buckets.values().each { result.addAll(it) }
        return result
    }

    void onException(List bucket, List objs, Throwable e) {
        log.warn("failed sort bucket ${bucket}", e)
    }

    void sortWithinBuckets(List bucket, List objs) {
        objs.sort(objectFieldComparator)
    }

    Map<List, List> sortBuckets() {
        buckets = buckets.sort(objectKeyComparator)
    }


}
