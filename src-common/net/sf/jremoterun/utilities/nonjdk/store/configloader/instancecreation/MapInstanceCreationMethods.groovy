package net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation

import groovy.transform.CompileStatic

@CompileStatic
class MapInstanceCreationMethods  implements InstanceCreationMethodsI<Map>{


     @Override
     Map createNew() {
          return [:]
     }

     @Override
     Map clone1(Map map) {
          return new HashMap(map)
     }
}
