package cn.edu.sjtu.cs.DBGroup;

import java.io.Serializable;

/**
 * Created by gefei on 16-4-16.
 */
public class MessageHeader implements Serializable {
    public final static  int PING = 0;
    public final static int PONG = 1;
    public final static int QUERY = 2 ;
    public final static int ADD = 3;
    public final static int OK = 4;
    public final static int BAD = 5;
    public final static int TRAVERSE = 6;
    public static final int REMOVE = 7;


    public static final int CLIENT_CREATE_FILE = 11;
    public static final int CLIENT_MAKE_DIRECTORY = 12;
    public static final int CLIENT_REMOVE_FILE = 13;
    public static final int CLIENT_REMOVE_DIRECTORY = 14;
    public static final int CLIENT_TRAVERSE = 15;
    public static final int CLIENT_LS = 16;

    public static final int CLIENT_CREATE_FILE_FAILURE = 21;
    public static final int CLIENT_MAKE_DIRECTORY_FAILURE = 22;
    public static final int CLIENT_REMOVE_FILE_FAILURE = 23;
    public static final int CLIENT_REMOVE_DIRECTORY_FAILURE = 24;
    public static final int CLIENT_TRAVERSE_RESPONSE = 25;
    public static final int CLIENT_LS_RESPONSE = 25;

    public static final int CLIENT_RESPONSE_BASE = 21;
    public static final String[] CLIENT_RESPONSE = {
            "File creation failure",
            "Directory creation failure",
            "File remove failure",
            "Directory remove failure"
    };

    public static final int CLIENT_META_DIST = 30;
    public static final int CLIENT_META_REQUEST = 31;
    public static final int CLIENT_META_DIST_RESPONSE = 32;
}