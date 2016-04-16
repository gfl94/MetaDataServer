package cn.edu.sjtu.cs.DBGroup;

import java.io.Serializable;

/**
 * Created by gefei on 16-4-16.
 */
public class FileMetaData implements Serializable {
    public String filename;
    public String modtime;
    public String vistime;
    public String privilege;
    public int size;

    public FileMetaData(String name, String modtime, String vistime, String pri, int size){
        this.filename = name;
        this.modtime = modtime;
        this.vistime = vistime;
        this.privilege = pri;
        this.size = size;
    }
}

//class Time {
//    public int year;
//    public int month;
//    public int day;
//    public int hour;
//    public int minute;
//    public int second;
//}
//
//class Privilege{
//    public String p;
//}