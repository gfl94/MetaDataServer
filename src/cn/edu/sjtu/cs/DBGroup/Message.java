package cn.edu.sjtu.cs.DBGroup;

import java.io.Serializable;

/**
 * Created by gefei on 16-4-16.
 */
public class Message implements Serializable{
    public int header;

    public FileMetaData metaData;

    public Message(int header, FileMetaData meta){
        this.header = header;
        this.metaData = meta;
    }

    public Message(){
        this.header = MessageHeader.OK;
        this.metaData = null;
    }
}