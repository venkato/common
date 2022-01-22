package net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation

import groovy.transform.CompileStatic

@CompileStatic
class ListInstanceCreationMethods implements InstanceCreationMethodsI<Collection>{


     @Override
     List createNew() {
          return []
     }

     @Override
     List clone1(Collection list) {
          return new ArrayList(list)
     }
}
