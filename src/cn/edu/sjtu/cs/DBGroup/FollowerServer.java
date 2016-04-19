package cn.edu.sjtu.cs.DBGroup;

/**
 * Created by gefei on 16-4-16.
 */
public class FollowerServer {

    private FileMetaData[] metaDatas;
    private final int STORAGETHRESHOLD = 100;
    private int current;
    public FollowerServerMessageHandler followerHandler = null;

    public static void main(String[] args){
        FollowerServer follower = new FollowerServer();
    }

    public FollowerServer(){
        metaDatas = new FileMetaData[STORAGETHRESHOLD];
        current = 0;
        followerHandler = new FollowerServerMessageHandler(this);
        followerHandler.setup();
        followerHandler.run();
    }

    public void remove(FileMetaData data){
        for (int i = 0; i < current; ++i){
            if (metaDatas[i] != null && metaDatas[i].filename.equals(data.filename)){
                metaDatas[i] = null;
                System.out.println(data.filename + "removed");
                break;
            }
        }
    }

    public void persistToFollower(FileMetaData data){
        System.out.println("follower persist");
        metaDatas[current++] = data;
    }

    public void traverse(){
        System.out.println("follower traverse");
        for (int i = 0; i < current; ++i){
            if (metaDatas[i] != null)
                System.out.println(metaDatas[i].filename);
        }
    }

}
