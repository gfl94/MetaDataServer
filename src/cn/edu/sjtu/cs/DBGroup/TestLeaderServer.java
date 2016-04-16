package cn.edu.sjtu.cs.DBGroup;

/**
 * Created by gefei on 16-4-16.
 */
public class TestLeaderServer {
    public static void main(String[] args){
        LeaderServer leader = new LeaderServer();
        leader.setNumberOfFollower(3);
        leader.setup();

        System.out.println("setup finished");
    }
}
