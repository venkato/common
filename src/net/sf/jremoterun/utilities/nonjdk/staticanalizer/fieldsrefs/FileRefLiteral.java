package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs;

public class FileRefLiteral {

    public FileRefLiteral parent;
    public String value;

    public FileRefLiteral(FileRefLiteral parent, String value) {
        this.parent = parent;
        this.value = value;
    }
}
