package cn.edu.sjtu.cs.DBGroup;

import java.io.Serializable;

/**
 * Created by gefei on 16-4-16.
 */
public class Message implements Serializable{
    public int header;
    public FileMetaData metaData;
    public String response;

    public Message(int header, FileMetaData data, String response){
        this.header = header;
        this.metaData = data;
        this.response = response;
    }

    public Message(int header, FileMetaData meta){
        this(header, meta, null);
    }

    public Message(int header, String response){
        this(header, null, response);
    }

    public Message(int header){
        this(header, null, null);
    }

    public Message(){
        this(MessageHeader.OK, null, null);
    }
}