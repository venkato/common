package net.sf.jremoterun.utilities.nonjdk.log;

import groovy.transform.CompileStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// !! must be java file to compile in idea
@CompileStatic
public class FileExtentionClass {

    public static File child(File parent, String c) {
        if (parent == null) {
            throw new IllegalArgumentException("parent file is null for child : "+c);
        }
        return new File(parent, c);
    }

    public static Path p(File f) throws IOException {
        return Paths.get( f.getCanonicalFile().getAbsolutePath() );
    }

    public static String getAbsolutePathUnix(File file) {
        return file.getAbsolutePath().replace('\\','/');
    }

    public static boolean isChildFile(File parent, File child) {
        if (parent == null) {
            throw new IllegalArgumentException("parent file is null for child : "+child);
        }
        if (child == null) {
            throw new IllegalArgumentException("child file is null for parent "+parent);
        }
        String parentPath = normalizePathChild(parent);
        String childPath = normalizePathChild(child);
        return childPath.startsWith(parentPath);
    }

    public static String normalizePathChild(File f){
        return f.getAbsolutePath().replace('\\', '/').toLowerCase();
    }

    public static String normalizePathPathToParent(File f) throws IOException {
        return f.getCanonicalFile().getAbsolutePath().replace('\\', '/');
    }

    /**
     * If parent and child same, return empty string
     * @throws IllegalArgumentException if not child
     */
    public static String getPathToParent(File parent, File child) throws IOException {
        if(!isChildFile(parent,child)){
            throw new IllegalArgumentException("child = "+child+" has incorrect parent : "+parent);
        }
        String parentPath = normalizePathPathToParent(parent);
        String childPath = normalizePathPathToParent(child);
        assert childPath.length() >= parentPath.length();
        String res = childPath.substring(parentPath.length());
        if (res.startsWith("/")) {
            return res.substring(1);
        }
        return res;
    }




}
