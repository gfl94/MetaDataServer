package cn.edu.sjtu.cs.DBGroup;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;

/**
 * Created by gefei on 16-4-16.
 */
public class FileMetaData implements Serializable{
    public String filename;
    public boolean isDirectory;
    public boolean isRegularFile;
    public boolean isSymbolicLink;
    public String createTime;
    public String accessTime;
    public String modifiedTime;
    public String permissions;

    public FileMetaData(String filename, boolean isDirectory, boolean isRegularFile,
                        boolean isSymbolicLink, String createTime, String accessTime,
                        String modifiedTime, String permissions){
        this.filename = filename;
        this.isDirectory = isDirectory;
        this.isRegularFile = isRegularFile;
        this.isSymbolicLink = isSymbolicLink;
        this.createTime = createTime;
        this.accessTime = accessTime;
        this.modifiedTime = modifiedTime;
        this.permissions = permissions;
    }

    public FileMetaData(String filename, boolean isDirectory, String createTime){
        this(filename, isDirectory, true, false, createTime, createTime, createTime, "");
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("filename: " + filename + "\n");
        sb.append("isDirectory: " + isDirectory + "\tisRegularFile: " + isRegularFile + "\tisSymbolicLink: " + isSymbolicLink + "\n");
        sb.append("createTime: " + createTime + "\n");
        sb.append("accessTime: " + accessTime + "\n");
        sb.append("modifiedTime: " + modifiedTime + "\n");
        return sb.toString();
    }
}