package cn.edu.sjtu.cs.DBGroup.test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;

/**
 * Created by gefei on 16-4-17.
 */
//public class ExtractFileMetaData {
//    public static void main(String[] args) throws IOException{
//        FileMetaData data = new FileMetaData("/home/gefei/project/IdeaProject/MetaDataServer/README.md");
//        try{
//            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./test"));
//            out.writeObject(data);
//            out.close();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        System.out.println("dump finished");
//
//        try{
//            ObjectInputStream in = new ObjectInputStream(new FileInputStream("./test"));
//            FileMetaData data2 = (FileMetaData) in.readObject();
//            System.out.println(data2.filename + "  " + data2.permissions);
//        }catch (IOException e){
//            e.printStackTrace();
//        }catch (ClassNotFoundException e){
//            e.printStackTrace();
//        }
//    }
//}
//class FileMetaData implements Serializable{
//    public String filename;
//    public boolean isDirectory;
//    public boolean isRegularFile;
//    public boolean isSymbolicLink;
//    public String createTime;
//    public String accessTime;
//    public String modifiedTime;
//    public String permissions;
//
//    public FileMetaData(String absolutePath){
//        Path path = new File(absolutePath).toPath();
//        try{
//            PosixFileAttributes attrs = Files.readAttributes(path, PosixFileAttributes.class);
//            this.filename = absolutePath;
//            this.isDirectory = attrs.isDirectory();
//            this.isRegularFile = attrs.isRegularFile();
//            this.isSymbolicLink = attrs.isSymbolicLink();
//            this.createTime = attrs.creationTime().toString();
//            this.accessTime = attrs.lastAccessTime().toString();
//            this.modifiedTime = attrs.lastModifiedTime().toString();
//            this.permissions = attrs.permissions().toString();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//}