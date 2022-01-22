package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PropsSimpleWriter {

    public boolean compareWithDefault = true;
    public Object sampleObject;
    public Object object;
    public List<Field> fieldsR;
    public StringBuilder lines = new StringBuilder();

    public String sep = "\n";

    public SimpleConverterStorage simpleConverterStorage = new SimpleConverterStorage();


    public PropsSimpleWriter(Object object) {
        this.object = object;
    }

    public Object fetchField(Field f) throws IllegalAccessException {
        return f.get(object);
    }

    public void findFields() {
        fieldsR = new ArrayList();
        Field[] fields = object.getClass().getFields();
        for (Field f : fields) {
            if (isAcceptField(f)) {
                fieldsR.add(f);
            }
        }
    }

    public boolean isAcceptField(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
    }

    public void writeFields() throws Exception {
        for (Field f : fieldsR) {
            try {
                writeOneField(f);
            } catch (Exception e) {
                onError(f, e);
                throw e;
            }
        }
    }

    public void writeOneField(Field f) throws Exception {
        Object fieldValue = f.get(object);
        boolean writeNeeded = true;
        if (writeNeeded && fieldValue == null) {
            writeNeeded = false;
        }
        if (writeNeeded && compareWithDefault) {
            if (compareObjects(f, fieldValue, f.get(sampleObject))) {
                writeNeeded = false;
            }
        }
        if (writeNeeded) {
            lines.append(createLine(f, fieldValue)).append(sep);
        }
    }


    public boolean compareObjects(Field f, Object obj1, Object obj2) {
        if (obj1 == null) {
            if (obj2 == null) {
                return true;
            }
            return false;
        }
        return obj1.equals(obj2);
    }

    public void onError(Field f, Exception e) {
        System.out.println("failed write field " + f.getName() + " " + e);
    }

    public String createLine(Field f, Object value) throws Exception {
        return f.getName() + " = " + simpleConverterStorage.initialConverter.convertToString(f, value);
    }

    public void convertAllToString() throws Exception {
        if (compareWithDefault && sampleObject == null) {
            sampleObject = object.getClass().newInstance();
        }
        findFields();
        writeFields();
    }

    public void saveToFileAll(File f) throws Exception {
        convertAllToString();
        saveToFileOnly(f);
    }

    public void saveToFileOnly(File f) throws IOException, InstantiationException, IllegalAccessException {
        FileWriter fileWriter = new FileWriter(f);
        fileWriter.write(lines.toString());
        fileWriter.flush();
        fileWriter.close();
    }


}
