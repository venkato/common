package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class PropsSimpleReader {

    public Object object;
    public Properties properties;
    public SimpleConverterStorage simpleConverterStorage = new SimpleConverterStorage();

    public PropsSimpleReader(Object object) {
        this.object = object;
    }

    public void readAndSet(File file) throws Exception {
        readFile(file);
        setFields();
    }

    public void readFromString(String s) throws IOException {
        StringReader fileReader = new StringReader(s);
        properties = new Properties();
        properties.load(fileReader);
        fileReader.close();
    }

    public void readFile(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        properties = new Properties();
        properties.load(fileReader);
        fileReader.close();
    }


    public void setFields() throws Exception {
        for (Map.Entry<Object, Object> entri : properties.entrySet()) {
            String key = (String) entri.getKey();
            String value = null;
            Field field = null;
            try {
                value = (String) entri.getValue();
                field = findField(key);
                setFieldOne(field, value);
            } catch (Exception e) {
                onError(key, value, field, e);
                throw e;
            }
        }
    }

    public void onError(String key, String value, Field field, Exception e) {
        System.out.println("failed read field " + key + " value = " + value + " " + e);
    }

    public void setFieldOne(Field field, String value) throws Exception {
        field.set(object, simpleConverterStorage.initialConverter.convertFromString(field, field.getType(),value));
    }

    public Field findField(String name) throws NoSuchFieldException {
        return object.getClass().getField(name);
    }


}
