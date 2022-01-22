package net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation

import groovy.transform.CompileStatic

@CompileStatic
interface InstanceCreationMethodsI<T> {



     T createNew()

     T clone1(T t)


}
