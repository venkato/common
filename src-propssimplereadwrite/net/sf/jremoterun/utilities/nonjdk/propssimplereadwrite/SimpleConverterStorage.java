package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite;

import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.customconverts.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleConverterStorage {

    public Map<String, ObjectToStringConverter> customConverts = new HashMap();
    public SimpleDefaultStringConverter initialConverter = new SimpleDefaultStringConverter(this);
    public SimpleList2StringConverter collectionConverter = new SimpleList2StringConverter(this);
    public ObjectToStringConverter<Enum> enumConverter = new SimpleEnum2StringConverter();

    public SimpleConverterStorage() {
        addCustomConverter(new SimpleFile2StringConverter());
        addCustomConverter(new SimpleClass2StringConverter());
    }

    public void addCustomConverter(ObjectToStringConverter converter) {
        customConverts.put(converter.getType().getName(), converter);
    }

}
