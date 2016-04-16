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

    public static final int CLIENTADD = 11;
    public static final int CLIENTTRAVERSE = 12;
    public static final int CLIENTREMOVE = 13;
}